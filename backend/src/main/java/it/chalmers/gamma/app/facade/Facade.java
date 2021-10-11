package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.usecase.AccessGuardUseCase;

public abstract class Facade {

    protected AccessGuardUseCase accessGuard;

    public Facade(AccessGuardUseCase accessGuard) {
        this.accessGuard = accessGuard;
    }

}
