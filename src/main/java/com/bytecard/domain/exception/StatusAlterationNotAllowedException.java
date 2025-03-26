package com.bytecard.domain.exception;

public class StatusAlterationNotAllowedException extends RuntimeException {

    public StatusAlterationNotAllowedException(String message) {
        super(message);
    }
}
