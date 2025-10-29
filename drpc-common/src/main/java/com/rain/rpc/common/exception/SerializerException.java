package com.rain.rpc.common.exception;

public class SerializerException extends RuntimeException {

    private static final long serialVersionUID = 5180965402390455947L;

    /**
     * Constructs a new SerializerException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public SerializerException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new SerializerException with the specified detail message.
     *
     * @param message the detail message
     */
    public SerializerException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SerializerException with the specified detail message and cause.
     *
     * @param message   the detail message
     * @param throwable the cause of the exception
     */
    public SerializerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}