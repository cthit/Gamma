package it.chalmers.gamma.app.domain.supergroup;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.common.Text;
import it.chalmers.gamma.app.domain.user.Name;

import java.util.Objects;

@RecordBuilder
public record SuperGroup(SuperGroupId id,
                         int version,
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
