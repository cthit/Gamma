package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.user.domain.Language;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ituser")
public class UserEntity extends MutableEntity<UUID> {

    @Id
    @Column(name = "user_id")
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

    @Column(name = "gdpr_training")
    protected boolean gdprTraining;

    @Column(name = "locked")
    protected boolean locked;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    protected UserAvatarEntity userAvatar;

    protected UserEntity() { }

    @Override
    public UUID getId() {
        return this.id;
    }

    public void acceptUserAgreement() {
        this.userAgreementAccepted = Instant.now();
    }

}