package it.chalmers.gamma.domain;

public class IDsNotMatchingException extends Exception {

    public IDsNotMatchingException() {
        super("The ID of the entity and the ID of the DTO does not match.");
    }
}
