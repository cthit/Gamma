package it.chalmers.gamma.app.common;

/**
 * Thrown when trying to save a domain object that has been modified by someone else since it was
 * loaded. Mutable domain objects carry a version, and an update is only accepted if the provided
 * version matches the currently stored version (optimistic locking).
 */
public class StaleDomainObjectException extends RuntimeException {

  public StaleDomainObjectException() {
    super("The object has been modified by someone else since it was loaded");
  }
}
