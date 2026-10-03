package com.utpcodefest.demo.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error body returned by every endpoint")
public record ApiError(@Schema(example = "title: Title is required") String error) {
}
