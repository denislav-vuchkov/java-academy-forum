package com.forum.javaforum.exceptions;

public class InvalidParameter extends RuntimeException{

    public InvalidParameter() {
    }

    public InvalidParameter(String message) {
        super(message);
    }
}
