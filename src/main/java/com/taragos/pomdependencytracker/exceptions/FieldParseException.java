package com.taragos.pomdependencytracker.exceptions;

/**
 * Thrown when a field could not be parsed.
 */
public class FieldParseException extends Exception {
    public FieldParseException(Exception e) {
        super(e);
    }
}
