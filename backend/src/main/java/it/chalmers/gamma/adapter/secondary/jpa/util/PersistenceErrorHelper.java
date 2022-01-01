package it.chalmers.gamma.adapter.secondary.jpa.util;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;

public final class PersistenceErrorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceErrorHelper.class);

    private PersistenceErrorHelper() {
    }

    //TODO: this can be refactored
    // DataIntegrityViolationException, should not be Exception
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

            //TODO: This should not happen, use lazy loading instead and get a PSQLException instead.
            if (t instanceof EntityNotFoundException e1) {
                e1.printStackTrace();
                return null;
            }

        }

        System.out.println("Hmmm");
        e.printStackTrace();

        throw new UnknownDataIntegrityViolationException();
    }

    public static class UnknownDataIntegrityViolationException extends RuntimeException {

    }


}
