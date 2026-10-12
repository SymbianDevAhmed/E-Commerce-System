package org.example;

public class EmptyCartException extends Exception {

    public EmptyCartException(String message) {
        super(message);
    }
}