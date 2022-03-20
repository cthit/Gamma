package it.chalmers.gamma.app.user.domain;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.Objects;

@RecordBuilder
public record GammaUser(UserId id,
                        Cid cid,
                        Nick nick,
                        FirstName firstName,
                        LastName lastName,
                        AcceptanceYear acceptanceYear,
                        Language language,
                        UserExtended extended) implements GammaUserBuilder.With {

    public GammaUser {
        Objects.requireNonNull(id);
        Objects.requireNonNull(cid);
        Objects.requireNonNull(nick);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(acceptanceYear);
    }

    public UserExtended extended() {
        if (this.extended == null) {
            throw new IllegalStateException("Extended is null, you should not try to access it, you've probably done something wrong");
        }

        return this.extended;
    }

}
