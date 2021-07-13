package it.chalmers.gamma.app;

public abstract class Facade {

    protected AccessGuard accessGuard;

    public Facade(AccessGuard accessGuard) {
        this.accessGuard = accessGuard;
    }

}
