package it.chalmers.gamma.adapter.secondary.jpa.user.gdpr;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "g_gdpr_trained")
public class GdprTrainedEntity extends ImmutableEntity<UUID> {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    private UserEntity user;

    protected GdprTrainedEntity() {}

    public GdprTrainedEntity(UserEntity userEntity) {
        this.userId = userEntity.getId();
        this.user = userEntity;
    }

    @Override
    public UUID getId() {
        return this.userId;
    }
}
