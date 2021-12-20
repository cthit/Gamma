package it.chalmers.gamma.app.supergroup.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.user.domain.Name;

import java.util.Objects;

@RecordBuilder
public record SuperGroup(SuperGroupId id,
                         int version,
                         Name name,
                         PrettyName prettyName,
                         SuperGroupType type,
                         Text description) {

    public SuperGroup {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(prettyName);
        Objects.requireNonNull(type);
        Objects.requireNonNull(description);
    }

}
