package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.user.gdpr.GdprTrainedEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.user.domain.Language;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "g_user")
public class UserEntity extends MutableEntity<UUID> {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    protected UUID id;

    @Column(name = "cid")
    protected String cid;

    @Column(name = "password")
    protected String password;

    @Column(name = "nick")
    protected String nick;

    @Column(name = "first_name")
    protected String firstName;

    @Column(name = "last_name")
    protected String lastName;

    @Column(name = "email")
    protected String email;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    protected Language language;

    @Column(name = "user_agreement_accepted")
    protected Instant userAgreementAccepted;

    @Column(name = "acceptance_year")
    protected int acceptanceYear;

    @Column(name = "locked")
    protected boolean locked;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    protected UserAvatarEntity userAvatar;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    protected GdprTrainedEntity gdprTrained;

    protected UserEntity() {
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public void acceptUserAgreement() {
        this.userAgreementAccepted = Instant.now();
    }

}