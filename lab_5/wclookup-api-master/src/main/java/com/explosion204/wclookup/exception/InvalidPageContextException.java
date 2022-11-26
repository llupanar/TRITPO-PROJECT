package com.explosion204.wclookup.exception;

public class InvalidPageContextException extends RuntimeException {
    private final ErrorType errorType;
    private final int invalidValue;

    public enum ErrorType {
        INVALID_PAGE_NUMBER,
        INVALID_PAGE_SIZE
    }

    public InvalidPageContextException(ErrorType errorType, int invalidValue) {
        this.errorType = errorType;
        this.invalidValue = invalidValue;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public int getInvalidValue() {
        return invalidValue;
    }
}
