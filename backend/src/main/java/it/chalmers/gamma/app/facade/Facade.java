package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;

public abstract class Facade {

    protected AccessGuard accessGuard;

    public Facade(AccessGuard accessGuard) {
        this.accessGuard = accessGuard;
    }

}
