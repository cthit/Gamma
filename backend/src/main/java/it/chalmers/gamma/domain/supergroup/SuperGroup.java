package it.chalmers.gamma.domain.supergroup;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.user.Name;

import java.util.Objects;

@RecordBuilder
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
