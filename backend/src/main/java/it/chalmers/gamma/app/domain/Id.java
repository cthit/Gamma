package it.chalmers.gamma.app.domain;

import java.io.Serializable;

public interface Id<S> extends Serializable {

    S getValue();

}
