package it.chalmers.gamma.adapter.secondary.jpa.user;

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

  @Column(name = "password", nullable = true, updatable = false)
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

  /*
   * The lock and the session revocation are only changed by UserJpaRepository.lock and unlock.
   * They are not updatable through saving the entity, so a save based on a read from before the
   * user was locked can't unlock them again, however the save and the lock interleave.
   */
  @Column(name = "locked", updatable = false)
  protected boolean locked;

  /** Sessions that signed in at or before this time are no longer valid. */
  @Column(name = "sessions_revoked_at", updatable = false)
  protected Instant sessionsRevokedAt;

  @Column(name = "email_recovery", updatable = false)
  protected boolean emailRecovery = true;

  @Column(name = "email_recovery_requires_verification", nullable = false, updatable = false)
  protected boolean emailRecoveryRequiresVerification;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  protected UserAvatarEntity userAvatar;

  protected UserEntity() {}

  @Override
  public UUID getId() {
    return this.id;
  }

  public String cid() {
    return this.cid;
  }

  public String email() {
    return this.email;
  }

  public String nick() {
    return this.nick;
  }

  public String firstName() {
    return this.firstName;
  }

  public String lastName() {
    return this.lastName;
  }

  public boolean locked() {
    return this.locked;
  }

  public void acceptUserAgreement() {
    this.userAgreementAccepted = Instant.now();
  }
}
