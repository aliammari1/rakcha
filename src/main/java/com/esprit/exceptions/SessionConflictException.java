package com.esprit.exceptions;

import com.esprit.models.cinemas.MovieSession;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SessionConflictException extends RuntimeException {

    private final MovieSession conflictingSession;

    public SessionConflictException(String message, MovieSession conflictingSession) {
        super(message);
        this.conflictingSession = conflictingSession;
    }

    public MovieSession getConflictingSession() {
        return conflictingSession;
    }
}
