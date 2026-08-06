package it.unibo.gameservermanager.instantiator.exceptions;

public class GameServerCheckException extends InstantiatorException {
    public GameServerCheckException(String message) {
        super(message);
    }

    public GameServerCheckException (Throwable cause) {
        super(cause);
    }
}
