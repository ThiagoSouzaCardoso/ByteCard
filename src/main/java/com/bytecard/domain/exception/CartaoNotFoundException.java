package com.bytecard.domain.exception;

public class CartaoNotFoundException extends  RuntimeException{

    public CartaoNotFoundException(String msg) {
        super(msg);
    }

}
