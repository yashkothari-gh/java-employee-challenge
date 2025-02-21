package com.reliaquest.api.exception;

public class EmployeeServiceException extends RuntimeException {
    public EmployeeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeeServiceException(String message) {
        super(message);
    }

}
