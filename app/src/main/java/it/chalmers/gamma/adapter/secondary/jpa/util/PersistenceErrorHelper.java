package it.chalmers.gamma.adapter.secondary.jpa.util;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION;
import static it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState.Type.NOT_UNIQUE;

public final class PersistenceErrorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceErrorHelper.class);

    private PersistenceErrorHelper() {
    }

    public static PersistenceErrorState getState(Exception e) {
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

            if (t instanceof EntityExistsException) {
                return new PersistenceErrorState(null, NOT_UNIQUE);
            }

            if (t instanceof EntityNotFoundException) {
                return new PersistenceErrorState(null, FOREIGN_KEY_VIOLATION);
            }

        }

        e.printStackTrace();

        throw new UnknownDataIntegrityViolationException();
    }

    public static class UnknownDataIntegrityViolationException extends RuntimeException {

    }


}
