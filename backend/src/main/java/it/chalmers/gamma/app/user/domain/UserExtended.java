package it.chalmers.gamma.app.user.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;

/**
 * Extension of User.
 * <p>
 * Usually, records in Gamma always have to be valid, but UserExtended is an exception.
 * To limit the amount of data that can accidentally be leaked, UserExtended can be partial complete.
 * For example, Goldapps needs to have access to email and gdprTrained, but not the rest of the data.
 */
@RecordBuilder
public record UserExtended(Email email,
                           int version,
                           boolean acceptedUserAgreement,
                           boolean gdprTrained,
                           boolean locked,
                           ImageUri avatarUri) implements UserExtendedBuilder.With {

}

