package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.Utils;

public class EntityAlreadyExistsException extends Exception {

    public EntityAlreadyExistsException() {
        super();
    }

    public EntityAlreadyExistsException(Class<? extends BaseEntity<?>> entity) {
        super(Utils.classToScreamingSnakeCase(entity) + "_ALREADY_EXISTS");
    }
}
