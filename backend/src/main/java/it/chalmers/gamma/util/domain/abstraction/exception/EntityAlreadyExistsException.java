package it.chalmers.gamma.util.domain.abstraction.exception;

import it.chalmers.gamma.util.ClassNameGeneratorUtils;
import it.chalmers.gamma.util.domain.abstraction.BaseEntity;

public class EntityAlreadyExistsException extends Exception {

    public EntityAlreadyExistsException() {
        super();
    }

    public EntityAlreadyExistsException(Class<? extends BaseEntity<?>> entity) {
        super(ClassNameGeneratorUtils.classToScreamingSnakeCase(entity) + "_ALREADY_EXISTS");
    }
}
