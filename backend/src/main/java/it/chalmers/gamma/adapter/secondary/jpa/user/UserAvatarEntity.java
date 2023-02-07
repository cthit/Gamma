package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_avatar_uri")
public class UserAvatarEntity extends MutableEntity<UUID> {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    protected UUID userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    protected UserEntity user;

    @Column(name = "avatar_uri")
    protected String avatarUri;

    protected UserAvatarEntity() {
    }

    @Override
    public UUID getId() {
        return this.userId;
    }

}
