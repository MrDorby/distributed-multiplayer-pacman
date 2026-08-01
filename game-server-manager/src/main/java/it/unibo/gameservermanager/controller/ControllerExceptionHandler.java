package it.unibo.gameservermanager.controller;

import it.unibo.gameservermanager.controller.exceptions.MatchmakerCommunicationException;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerCheckException;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(GameServerInstantiationException.class)
    public ResponseEntity<String> handleGameServerInstantiationException(GameServerInstantiationException e) {
        String msg = "Error with the instantiation of the GameServer: " + e.getMessage();
        this.logger.error(msg);
        return ResponseEntity
                .internalServerError()
                .body(msg);
    }

    @ExceptionHandler(GameServerCheckException.class)
    public ResponseEntity<String> handleGameServerCheckException(GameServerCheckException e) {
        String msg = "Error while checking the status of the GameServer: " + e.getMessage();
        this.logger.error(msg);
        return ResponseEntity
                .internalServerError()
                .body(msg);
    }

    @ExceptionHandler(MatchmakerCommunicationException.class)
    public ResponseEntity<String> handleMatchmakerCommunicationException(MatchmakerCommunicationException e) {
        String msg = "Error during communication with the Matchmaker: " + e.getMessage();
        this.logger.error(msg);
        return ResponseEntity
                .internalServerError()
                .body(msg);
    }
}
