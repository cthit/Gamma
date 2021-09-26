package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Objects;

public interface Id<S> extends Serializable {

    S getValue();

}
