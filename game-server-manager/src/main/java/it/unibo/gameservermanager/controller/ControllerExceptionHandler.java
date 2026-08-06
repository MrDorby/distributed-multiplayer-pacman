package it.unibo.gameservermanager.controller;

import it.unibo.gameservermanager.controller.exceptions.MatchmakerCommunicationException;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerCheckException;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerInstantiationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    private ResponseEntity<String> showErrorMessage(String errorMessage, Exception e) {
        String message = errorMessage + e;
        this.logger.error(message);
        this.logger.error("Stack trace: {}", ExceptionUtils.getStackTrace(e));
        return ResponseEntity
                .internalServerError()
                .body(message);
    }

    @ExceptionHandler(GameServerInstantiationException.class)
    public ResponseEntity<String> handleGameServerInstantiationException(GameServerInstantiationException e) {
        return showErrorMessage("Error with the instantiation of the GameServer: ", e);
    }

    @ExceptionHandler(GameServerCheckException.class)
    public ResponseEntity<String> handleGameServerCheckException(GameServerCheckException e) {
        return showErrorMessage("Error while checking the status of the GameServer: ", e);
    }

    @ExceptionHandler(MatchmakerCommunicationException.class)
    public ResponseEntity<String> handleMatchmakerCommunicationException(MatchmakerCommunicationException e) {
        return showErrorMessage("Error during communication with the Matchmaker: ", e);
    }
}
