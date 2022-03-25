package com.forum.javaforum.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, int id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s does not exist.", type, attribute, value));
    }

    public EntityNotFoundException(String type) {
        super(String.format("No %s found in the system.", type));
    }

    public EntityNotFoundException(String type, String message) {
        super(String.format("No %s with the %s have been found in the system.", type, message));
    }

    public EntityNotFoundException() {
    }
}
