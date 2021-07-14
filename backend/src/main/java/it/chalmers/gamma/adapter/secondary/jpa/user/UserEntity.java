package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.AcceptanceYear;
import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.FirstName;
import it.chalmers.gamma.app.domain.Language;
import it.chalmers.gamma.app.domain.LastName;
import it.chalmers.gamma.app.domain.Nick;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.user.Password;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ituser")
public class UserEntity extends MutableEntity<UserId, User> {

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

    @Column(name = "user_agreement")
    private boolean userAgreement;

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

    protected User toDomain() {
        return new User(
                UserId.valueOf(this.id),
                Cid.valueOf(this.cid),
                new Email(this.email),
                this.language,
                new Nick(this.nick),
                new FirstName(this.firstName),
                new LastName(this.lastName),
                this.userAgreement,
                new AcceptanceYear(this.acceptanceYear)
        );
    }

    @Override
    protected UserId id() {
        return UserId.valueOf(this.id);
    }

    @Override
    public void apply(User u) {
        assert(this.id == u.id().value());
        assert(this.cid.equals(u.cid().value()));

        this.acceptanceYear = u.acceptanceYear().value();
        this.email = u.email().value();
        this.firstName = u.firstName().value();
        this.lastName = u.lastName().value();
        this.language = u.language();
        this.nick = u.nick().value();
        this.userAgreement = u.userAgreement();
    }

    protected void setPassword(Password password) {
        this.password = password.value();
    }

    protected Password getPassword() {
        return new Password(this.password);
    }

    protected void resetUserAgreement() {
        this.userAgreement = false;
    }

    protected UserId getId() {
        return UserId.valueOf(this.id);
    }
}