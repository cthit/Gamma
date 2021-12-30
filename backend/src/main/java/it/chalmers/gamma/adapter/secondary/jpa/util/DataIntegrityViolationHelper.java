package it.chalmers.gamma.adapter.secondary.jpa.util;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

public final class DataIntegrityViolationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataIntegrityViolationHelper.class);

    private DataIntegrityViolationHelper() {}

    //TODO: this can be refactored
    public static DataIntegrityErrorState getState(DataIntegrityViolationException e) {
        for (Throwable t = e.getCause(); t != null; t = t.getCause()) {

            if (PSQLException.class.equals(t.getClass())) {
                PSQLException postgresException = (PSQLException) t;

                ServerErrorMessage serverErrorMessage = postgresException.getServerErrorMessage();
                if (serverErrorMessage != null) {
                    for (DataIntegrityErrorState.Type type : DataIntegrityErrorState.Type.values()) {
                        if (type.ERROR_CODE.equals(serverErrorMessage.getSQLState())) {
                            return new DataIntegrityErrorState(serverErrorMessage.getConstraint(), type);
                        }
                    }
                    LOGGER.error("Cannot produce a DataIntegrityState with ServerErrorMessage that has constaint="
                            + serverErrorMessage.getConstraint()
                            + " and sql error state="
                            + serverErrorMessage.getSQLState()
                    );
                    LOGGER.error("DataIntegrityViolationException: ", e);
                } else {
                    LOGGER.error("ServerErrorMessage is null for DataIntegrityViolationException", e);
                }
            }
        }

        throw new UnknownDataIntegrityViolationException();
    }

    public static class UnknownDataIntegrityViolationException extends RuntimeException {

    }


}
