package com.wipro.restaurant.exception;

public class TableUnavailableException extends RuntimeException {

    public TableUnavailableException(String message) {
        super(message);
    }
}
