package it.chalmers.gamma.app.domain;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.Instant;
import java.util.Objects;

@RecordBuilder
public record User(UserId id,
                   Cid cid,
                   Email email,
                   Language language,
                   Nick nick,
                   FirstName firstName,
                   LastName lastName,
                   Instant lastAcceptedUserAgreement,
                   AcceptanceYear acceptanceYear,
                   boolean gdprTrained,
                   boolean locked,
                   ImageUri imageUri) //TODO: Add authorities
        implements UserBuilder.With {

    public User {
        Objects.requireNonNull(id);
        Objects.requireNonNull(cid);
        Objects.requireNonNull(email);
        Objects.requireNonNull(language);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(acceptanceYear);
    }

}
