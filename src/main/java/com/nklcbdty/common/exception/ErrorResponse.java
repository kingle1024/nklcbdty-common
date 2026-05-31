package com.nklcbdty.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String status;
    private String message;
}
