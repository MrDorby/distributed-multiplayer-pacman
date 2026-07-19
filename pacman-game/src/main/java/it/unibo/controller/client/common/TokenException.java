package it.unibo.controller.client.common;

/**
 * TokenException thrown when the token check gives a failure.
 */
public class TokenException extends Exception {

    public TokenException() {

    }

    public TokenException(String message) {
        super(message);
    }
}
