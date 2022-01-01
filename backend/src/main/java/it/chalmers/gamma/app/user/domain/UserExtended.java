
package it.chalmers.gamma.app.user.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;

import java.util.Objects;

@RecordBuilder
public record UserExtended(Email email,
                           int version,
                           Language language,
                           Password password,
                           boolean acceptedUserAgreement,
                           boolean gdprTrained,
                           boolean locked,
                           ImageUri avatarUri) implements UserExtendedBuilder.With {

    public UserExtended {
        Objects.requireNonNull(email);
        Objects.requireNonNull(language);
        Objects.requireNonNull(password);
    }

}

