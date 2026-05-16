package com.taskmanagement.web.exception;

public class ErrorResponse {
    private java.time.LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private java.util.Map<String, String> fieldErrors;

    public ErrorResponse(int status, String error, String message) {
        this.timestamp = java.time.LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(int status, String error, String message, java.util.Map<String, String> fieldErrors) {
        this.timestamp = java.time.LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public java.time.LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public java.util.Map<String, String> getFieldErrors() { return fieldErrors; }
}