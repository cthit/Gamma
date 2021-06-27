package it.chalmers.gamma.app.useravatar.service;

import it.chalmers.gamma.app.domain.ImageUri;
import it.chalmers.gamma.app.domain.UserAvatar;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_avatar_uri")
public class UserAvatarEntity extends MutableEntity<UserId, UserAvatar> {

    @EmbeddedId
    private UserId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "avatar_uri"))
    private ImageUri avatarUri;

    protected UserAvatarEntity() { }

    protected UserAvatarEntity(UserAvatar userAvatar) {
        assert (userAvatar.userId() != null);

        this.id = userAvatar.userId();

        apply(userAvatar);
    }

    @Override
    protected UserId id() {
        return this.id;
    }

    @Override
    protected UserAvatar toDTO() {
        return new UserAvatar(
                this.id,
                this.avatarUri
        );
    }

    @Override
    protected void apply(UserAvatar userAvatar) {
        assert (this.id == userAvatar.userId());

        this.avatarUri = userAvatar.avatarUri();
    }
}
