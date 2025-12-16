package br.com.socialmedia.socialmedia.dto.exception;

import java.time.Instant;
import java.util.List;

public class ErrorDetails {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldErrorResponse> fieldErrors;

    public ErrorDetails(Instant timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorDetails(Instant timestamp, int status, String error, String message, String path,
                        List<FieldErrorResponse> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<FieldErrorResponse> getFieldErrors() { return fieldErrors; }
}