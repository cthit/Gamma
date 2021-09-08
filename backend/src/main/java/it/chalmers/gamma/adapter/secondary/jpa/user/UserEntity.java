package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.domain.user.AcceptanceYear;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.user.FirstName;
import it.chalmers.gamma.domain.user.Language;
import it.chalmers.gamma.domain.user.LastName;
import it.chalmers.gamma.domain.user.Nick;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.domain.user.Password;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ituser")
public class UserEntity extends MutableEntity<UserId> {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "cid")
    private String cid;

    @Column(name = "password")
    private String password;

    @Column(name = "nick")
    private String nick;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "user_agreement_accepted")
    private Instant userAgreementAccepted;

    @Column(name = "acceptance_year")
    private int acceptanceYear;

    protected UserEntity() { }

    protected UserEntity(User user) {
        assert(user.id() != null);
        assert(user.cid() != null);

        this.id = user.id().value();
        this.cid = user.cid().value();

        this.apply(user);
    }

    public User toDomain() {
        return new User(
                UserId.valueOf(this.id),
                Cid.valueOf(this.cid),
                new Email(this.email),
                this.language,
                new Nick(this.nick),
                new Password(this.password),
                new FirstName(this.firstName),
                new LastName(this.lastName),
                this.userAgreementAccepted,
                new AcceptanceYear(this.acceptanceYear),
                false,
                false,
                null
        );
        //TODO add one to one
    }

    @Override
    protected UserId id() {
        return UserId.valueOf(this.id);
    }

    public void apply(User u) {
        assert(this.id == u.id().value());
        assert(this.cid.equals(u.cid().value()));

        this.acceptanceYear = u.acceptanceYear().value();
        this.email = u.email().value();
        this.firstName = u.firstName().value();
        this.lastName = u.lastName().value();
        this.language = u.language();
        this.nick = u.nick().value();
//        this.userAgreementAccepted = u.userAgreementAccepted();
        //TODO: Add gdpr locked and imageUri
    }

    protected void setPassword(Password password) {
        this.password = password.value();
    }

    protected Password getPassword() {
        return new Password(this.password);
    }

    protected UserId getId() {
        return UserId.valueOf(this.id);
    }
}