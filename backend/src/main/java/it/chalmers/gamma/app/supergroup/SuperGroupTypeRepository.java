package it.chalmers.gamma.app.supergroup;

public interface SuperGroupTypeRepository {

    class SuperGroupAlreadyExistsException extends Exception { }
    class SuperGroupNotFoundException extends Exception { }
    class SuperGroupHasUsagesException extends Exception { }

}
