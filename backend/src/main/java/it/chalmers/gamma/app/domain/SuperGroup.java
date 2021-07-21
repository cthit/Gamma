package it.chalmers.gamma.app.domain;

import java.util.Objects;

public record SuperGroup(SuperGroupId id,
                         Name name,
                         PrettyName prettyName,
                         SuperGroupType type,
                         Email email,
                         Text description) {

    public SuperGroup {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(type);
        Objects.requireNonNull(email);
        Objects.requireNonNull(description);
    }

}
