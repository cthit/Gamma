package it.chalmers.gamma.adapter.secondary.jpa.util;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState.Type.NOT_UNIQUE;

public final class PersistenceErrorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceErrorHelper.class);

    private PersistenceErrorHelper() {
    }

    //TODO: this can be refactored
    // DataIntegrityViolationException, should not be Exception
    public static PersistenceErrorState getState(Exception e) {
        List<PersistenceErrorState> states = new ArrayList<>();

        for (Throwable t = e.getCause(); t != null; t = t.getCause()) {

            //If there's a specific PQLException such as a UNIQUE violation
            if (t instanceof PSQLException postgresException) {
                ServerErrorMessage serverErrorMessage = postgresException.getServerErrorMessage();
                if (serverErrorMessage != null) {
                    for (PersistenceErrorState.Type type : PersistenceErrorState.Type.values()) {
                        if (type.ERROR_CODE.equals(serverErrorMessage.getSQLState())) {
                            return new PersistenceErrorState(serverErrorMessage.getConstraint(), type);
                        }
                    }
                }
            }

            if (t instanceof EntityExistsException e1) {
                return new PersistenceErrorState(null, NOT_UNIQUE);
            }

        }

        e.printStackTrace();

        throw new UnknownDataIntegrityViolationException();
    }

    public static class UnknownDataIntegrityViolationException extends RuntimeException {

    }


}
