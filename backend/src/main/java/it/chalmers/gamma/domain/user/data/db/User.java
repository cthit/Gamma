package it.chalmers.gamma.domain.user.data.db;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.data.dto.UserDTO;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import java.time.Year;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "ituser")
public class User implements MutableEntity<UserDTO> {

    @EmbeddedId
    private UserId id;

    @Embedded
    private Cid cid;

    @Column(name = "password")
    private String password;

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

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "user_agreement")
    private boolean userAgreement;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "acceptance_year")
    private int acceptanceYear;

    protected User() { }

    public User(UserDTO user) {
        assert(user.getId() != null);
        assert(user.getCid() != null);

        this.id = user.getId();
        this.cid = user.getCid();

        this.apply(user);
    }

    public UserDTO toDTO() {
        return new UserDTO.UserDTOBuilder()
                .acceptanceYear(Year.of(this.acceptanceYear))
                .activated(this.activated)
                .avatarUrl(this.avatarUrl)
                .cid(this.cid)
                .email(this.email)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .id(this.id)
                .nick(this.nick)
                .userAgreement(this.userAgreement)
                .language(this.language)
                .build();
    }

    @Override
    public void apply(UserDTO u) {
        assert(this.id == u.getId());
        assert(this.cid == u.getCid());

        this.acceptanceYear = u.getAcceptanceYear().getValue();
        this.activated = u.isActivated();
        this.avatarUrl = u.getAvatarUrl();
        this.email = u.getEmail();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.language = u.getLanguage();
        this.nick = u.getNick();
        this.userAgreement = u.isUserAgreement();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public UserId getId() {
        return id;
    }

    public Cid getCid() {
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
                && Objects.equals(this.avatarUrl, user.avatarUrl)
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
                this.avatarUrl,
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
                ", avatarUrl='" + avatarUrl + '\'' +
                ", userAgreement=" + userAgreement +
                ", activated=" + activated +
                ", acceptanceYear=" + acceptanceYear +
                '}';
    }
}
