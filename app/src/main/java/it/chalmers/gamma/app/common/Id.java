package it.chalmers.gamma.app.common;

import java.io.Serializable;

public interface Id<S> extends Serializable {

  S getValue();
}
