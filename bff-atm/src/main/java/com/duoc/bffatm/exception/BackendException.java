package com.duoc.bffatm.exception;

public class BackendException extends RuntimeException {

    private final int status;

    public BackendException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
