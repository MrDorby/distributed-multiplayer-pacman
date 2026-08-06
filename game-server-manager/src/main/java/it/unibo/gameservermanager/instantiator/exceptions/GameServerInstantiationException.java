package it.unibo.gameservermanager.instantiator.exceptions;

public class GameServerInstantiationException extends InstantiatorException {
    public GameServerInstantiationException(String message) {
        super(message);
    }

    public GameServerInstantiationException (Throwable cause) {
        super(cause);
    }
}
