package com.taragos.pomdt.exceptions;

/**
 * Thrown when a field could not be parsed.
 */
public class FieldParseException extends Exception {
    public FieldParseException(Exception e) {
        super(e);
    }
}
