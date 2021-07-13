package it.chalmers.gamma.app.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

@RecordBuilder
public record User(UserId id,
                   Cid cid,
                   Email email,
                   Language language,
                   Nick nick,
                   FirstName firstName,
                   LastName lastName,
                   boolean userAgreement,
                   AcceptanceYear acceptanceYear)
        implements DTO, UserBuilder.With {

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
