package it.chalmers.gamma.app.domain.user;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.ImageUri;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@RecordBuilder
public record User(UserId id,
                   int version,
                   Cid cid,
                   Email email,
                   Language language,
                   Nick nick,
                   Password password,
                   FirstName firstName,
                   LastName lastName,
                   Instant lastAcceptedUserAgreement,
                   AcceptanceYear acceptanceYear,
                   boolean gdprTrained,
                   boolean locked,
                   Optional<ImageUri> avatarUri)
    implements UserBuilder.With {

    public User {
        Objects.requireNonNull(id);
        Objects.requireNonNull(cid);
        Objects.requireNonNull(email);
        Objects.requireNonNull(language);
        Objects.requireNonNull(password);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(acceptanceYear);
        Objects.requireNonNull(avatarUri);
    }

}
