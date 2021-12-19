package it.chalmers.gamma.app;

import it.chalmers.gamma.app.authentication.AccessGuard;

public abstract class Facade {

    protected AccessGuard accessGuard;

    public Facade(AccessGuard accessGuard) {
        this.accessGuard = accessGuard;
    }

}
