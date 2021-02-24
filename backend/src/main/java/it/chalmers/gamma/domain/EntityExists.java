package it.chalmers.gamma.domain;

public interface EntityExists <T extends Id> {

    boolean exists(T t);

}
