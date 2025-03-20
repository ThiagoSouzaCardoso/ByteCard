package com.bytecard.domain.exception;

public class ClienteNotFoundException extends  RuntimeException{

    public ClienteNotFoundException(String msg) {
        super(msg);
    }

}
