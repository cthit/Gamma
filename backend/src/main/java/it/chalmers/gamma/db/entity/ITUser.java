package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import it.chalmers.gamma.domain.Language;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ituser")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ITUser implements UserDetails{

    @Id
    @Column(updatable = false)
    @JsonIgnore
    private UUID id;

    @Column(name = "cid", length = 10, nullable = false, unique = true)
    private String cid;

    @JsonIgnore
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nick", length = 50)
    private String nick;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "language", length = 15)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "avatarUrl", length = 255)
    private String avatarUrl;

    @Column(name = "gdpr", nullable = false)
    private boolean gdpr;

    @Column(name = "user_agreement", nullable = false)
    private boolean userAgreement;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked;

    @Column(name = "acceptance_year", nullable = false)
    private int acceptanceYear;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;


    public ITUser() {
        id = UUID.randomUUID();
        createdAt = Instant.now();
        lastModifiedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return cid;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isGdpr() {
        return gdpr;
    }

    public void setGdpr(boolean gdpr) {
        this.gdpr = gdpr;
    }

    public boolean isUserAgreement() {
        return userAgreement;
    }

    public void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    public Year getAcceptanceYear() {
        return Year.of(acceptanceYear);
    }

    public void setAcceptanceYear(Year acceptanceYear) {
        this.acceptanceYear = acceptanceYear.getValue();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ITUser itUser = (ITUser) o;
        return gdpr == itUser.gdpr &&
                userAgreement == itUser.userAgreement &&
                Objects.equals(id, itUser.id) &&
                Objects.equals(cid, itUser.cid) &&
                Objects.equals(nick, itUser.nick) &&
                Objects.equals(password, itUser.password) &&
                Objects.equals(firstName, itUser.firstName) &&
                Objects.equals(lastName, itUser.lastName) &&
                Objects.equals(email, itUser.email) &&
                Objects.equals(phone, itUser.phone) &&
                Objects.equals(language, itUser.language) &&
                Objects.equals(avatarUrl, itUser.avatarUrl) &&
                Objects.equals(acceptanceYear, itUser.acceptanceYear) &&
                Objects.equals(createdAt, itUser.createdAt) &&
                Objects.equals(lastModifiedAt, itUser.lastModifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cid, password, nick, firstName, lastName, email, phone, language, avatarUrl, gdpr, userAgreement, acceptanceYear, createdAt, lastModifiedAt);
    }

    @Override
    public String toString() {
        return "ITUser{" +
                "id=" + id +
                ", cid='" + cid + '\'' +
                ", pass='<redacted>'" +
                ", nick='" + nick + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", language='" + (language != null ? language.toString() : "No language set") + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gdpr=" + gdpr +
                ", accountLocked=" + accountLocked +
                ", userAgreement=" + userAgreement +
                ", acceptanceYear=" + acceptanceYear +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
    public ITUserView getView(List<String> props){
        ITUserView view = new ITUserView();
        for(String prop : props) {
            switch (prop){
                case "id":
                    view.setId(this.id);
                    break;
                case "cid":
                    view.setCid(this.cid);
                    break;
                case "nick":
                    view.setNick(this.nick);
                    break;
                case "firstName":
                    view.setFirstName(this.firstName);
                    break;
                case "lastname":
                    view.setLastName(this.lastName);
                    break;
                case "email":
                    view.setEmail(this.email);
                    break;
                case "phone":
                    view.phone = this.phone;
                    break;
                case "language":
                    view.language = this.language;
                    break;
                case "avatarURL":
                    view.avatarUrl = this.avatarUrl;
                    break;
                case "gdpr":
                    view.gdpr = this.gdpr;
                    break;
                case "userAgreement":
                    view.userAgreement = this.userAgreement;
                    break;
                case "accountLocked":
                    view.accountLocked = this.accountLocked;
                    break;
                case "acceptanceYear":
                    view.acceptanceYear = this.acceptanceYear;
                    break;
                case "createdAt":
                    view.createdAt = this.createdAt;
                    break;
                case "lastModifiedAt":
                    view.lastModifiedAt = this.lastModifiedAt;
                    break;
            }
        }
        return view;
    }
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class ITUserView{
        private UUID id;
        private String cid;
        private String nick;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private Language language;
        private String avatarUrl;
        private boolean gdpr;
        private boolean userAgreement;
        private boolean accountLocked;
        private int acceptanceYear;
        private Instant createdAt;
        private Instant lastModifiedAt;


        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getCid() {
            return cid;
        }

        public String getNick() {
            return nick;
        }
        public String getFirstName() {
            return firstName;
        }
        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public Language getLanguage() {
            return language;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public boolean isGdpr() {
            return gdpr;
        }

        public boolean isUserAgreement() {
            return userAgreement;
        }

        public boolean isAccountLocked() {
            return accountLocked;
        }

        public int getAcceptanceYear() {
            return acceptanceYear;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Instant getLastModifiedAt() {
            return lastModifiedAt;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setLanguage(Language language) {
            this.language = language;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public void setGdpr(boolean gdpr) {
            this.gdpr = gdpr;
        }

        public void setUserAgreement(boolean userAgreement) {
            this.userAgreement = userAgreement;
        }

        public void setAccountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
        }

        public void setAcceptanceYear(int acceptanceYear) {
            this.acceptanceYear = acceptanceYear;
        }

        public void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        public void setLastModifiedAt(Instant lastModifiedAt) {
            this.lastModifiedAt = lastModifiedAt;
        }
    }
}
