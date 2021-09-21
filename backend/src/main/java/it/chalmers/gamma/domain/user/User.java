package it.chalmers.gamma.domain.user;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.common.ImageUri;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RecordBuilder
public record User(UserId id,
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
                   ImageUri imageUri,
                   List<UserAuthority> authorities,
                   List<GroupMember> groups)
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
        Objects.requireNonNull(authorities);
    }

}
