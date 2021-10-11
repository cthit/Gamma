package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "user_avatar_uri")
public class UserAvatarEntity extends MutableEntity<UserId> {

    @Id
    @Column(name = "user_id")
    protected UUID userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    protected UserEntity user;

    @Column(name = "avatar_uri")
    protected String avatarUri;

    protected UserAvatarEntity() { }

    @Override
    protected UserId domainId() {
        return this.user.domainId();
    }

    protected String getAvatarUri() {
        return this.avatarUri;
    }

}
