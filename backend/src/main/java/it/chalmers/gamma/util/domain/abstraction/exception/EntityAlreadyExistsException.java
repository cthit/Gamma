package it.chalmers.gamma.util.domain.abstraction.exception;

import it.chalmers.gamma.util.ClassNameGeneratorUtils;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

public class EntityAlreadyExistsException extends Exception {

    public EntityAlreadyExistsException() {
        super();
    }

    public EntityAlreadyExistsException(Class<? extends MutableEntity<?, ?>> entity) {
        super(ClassNameGeneratorUtils.classToScreamingSnakeCase(entity) + "_ALREADY_EXISTS");
    }
}
