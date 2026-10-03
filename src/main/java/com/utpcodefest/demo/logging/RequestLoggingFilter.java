package com.utpcodefest.demo.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Highest precedence: runs before Spring Security, so 401/403 responses are logged too.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger log = LogManager.getLogger(RequestLoggingFilter.class);
	private static final String REQUEST_ID = "requestId";
	private static final Pattern SAFE_REQUEST_ID = Pattern.compile("[A-Za-z0-9-]{1,64}");

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !request.getRequestURI().startsWith("/api/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String requestId = requestId(request);
		ThreadContext.put(REQUEST_ID, requestId);
		response.setHeader("X-Request-Id", requestId);

		long started = System.nanoTime();
		boolean failed = false;
		try {
			chain.doFilter(request, response);
		} catch (IOException | ServletException | RuntimeException e) {
			failed = true;
			throw e;
		} finally {
			long ms = (System.nanoTime() - started) / 1_000_000;
			int status = failed ? 500 : response.getStatus();
			Level level = status >= 500 ? Level.ERROR : status >= 400 ? Level.WARN : Level.INFO;
			String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
			log.log(level, "{} {}{} -> {} ({} ms) ip={}",
					request.getMethod(), request.getRequestURI(), query, status, ms, clientIp(request));
			ThreadContext.remove(REQUEST_ID);
		}
	}

	// Reuse the caller's X-Request-Id for tracing, but only if it can't inject junk into the logs.
	private static String requestId(HttpServletRequest request) {
		String incoming = request.getHeader("X-Request-Id");
		if (incoming != null && SAFE_REQUEST_ID.matcher(incoming).matches()) {
			return incoming;
		}
		return UUID.randomUUID().toString().substring(0, 8);
	}

	// Behind Render's proxy the socket address is the proxy; the real client is first in X-Forwarded-For.
	private static String clientIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}
