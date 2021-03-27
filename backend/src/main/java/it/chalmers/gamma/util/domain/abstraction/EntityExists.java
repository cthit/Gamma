package it.chalmers.gamma.util.domain.abstraction;

public interface EntityExists <T extends Id> {

    boolean exists(T t);

}
