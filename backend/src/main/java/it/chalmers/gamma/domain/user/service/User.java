package it.chalmers.gamma.domain.user.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import java.time.Year;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "ituser")
public class User extends MutableEntity<UserDTO> {

    @EmbeddedId
    private UserId id;

    @Embedded
    private Cid cid;

    @Embedded
    private Password password;

    @Column(name = "nick")
    private String nick;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Embedded
    private Email email;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "user_agreement")
    private boolean userAgreement;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "acceptance_year")
    private int acceptanceYear;

    protected User() { }

    protected User(UserDTO user) {
        assert(user.id() != null);
        assert(user.cid() != null);

        this.id = user.id();
        this.cid = user.cid();

        this.apply(user);
    }

    protected UserDTO toDTO() {
        return new UserDTO(
                this.id,
                this.cid,
                this.email,
                this.language,
                this.nick,
                this.firstName,
                this.lastName,
                this.userAgreement,
                Year.of(this.acceptanceYear),
                this.activated
        );
    }

    @Override
    protected void apply(UserDTO u) {
        assert(this.id == u.id());
        assert(this.cid == u.cid());

        this.acceptanceYear = u.acceptanceYear().getValue();
        this.activated = u.activated();
        this.email = u.email();
        this.firstName = u.firstName();
        this.lastName = u.lastName();
        this.language = u.language();
        this.nick = u.nick();
        this.userAgreement = u.userAgreement();
    }

    protected void setPassword(Password password) {
        this.password = password;
    }

    protected Password getPassword() {
        return password;
    }

    protected UserId getId() {
        return id;
    }

    protected Cid getCid() {
        return cid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return this.userAgreement == user.userAgreement
                && Objects.equals(this.id, user.id)
                && Objects.equals(this.cid, user.cid)
                && Objects.equals(this.nick, user.nick)
                && Objects.equals(this.password, user.password)
                && Objects.equals(this.firstName, user.firstName)
                && Objects.equals(this.lastName, user.lastName)
                && Objects.equals(this.email, user.email)
                && Objects.equals(this.language, user.language)
                && Objects.equals(this.acceptanceYear, user.acceptanceYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.cid,
                this.password,
                this.nick,
                this.firstName,
                this.lastName,
                this.email,
                this.language,
                this.userAgreement,
                this.acceptanceYear);
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", cid='" + cid + '\'' +
                ", password='" + password + '\'' +
                ", nick='" + nick + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", language=" + language +
                ", userAgreement=" + userAgreement +
                ", activated=" + activated +
                ", acceptanceYear=" + acceptanceYear +
                '}';
    }
}
