package it.chalmers.gamma.adapter.secondary.jpa.user.admin;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "g_admin_user")
public class AdminEntity extends ImmutableEntity<UUID> {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    protected AdminEntity() {}

    protected AdminEntity(UUID userId) {
        this.userId = userId;
    }

    @Override
    public UUID getId() {
        return this.userId;
    }
}
