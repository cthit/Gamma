package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.ImageUri;
import it.chalmers.gamma.app.domain.UserAvatar;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_avatar_uri")
public class UserAvatarEntity extends MutableEntity<UserId, UserAvatar> {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "avatar_uri")
    private String avatarUri;

    protected UserAvatarEntity() { }

    protected UserAvatarEntity(UserAvatar userAvatar) {
        this.user = new UserEntity(userAvatar.user());

        apply(userAvatar);
    }

    @Override
    protected UserId id() {
        return this.user.id();
    }

    @Override
    protected UserAvatar toDomain() {
        return new UserAvatar(
                this.user.toDomain(),
                new ImageUri(this.avatarUri)
        );
    }

    @Override
    protected void apply(UserAvatar userAvatar) {
        assert (this.user.id() == userAvatar.user().id());

        this.avatarUri = userAvatar.avatarUri().value();
    }
}
