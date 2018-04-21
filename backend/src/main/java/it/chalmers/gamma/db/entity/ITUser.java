package it.chalmers.gamma.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.time.Year;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ituser")
public class ITUser {

    @Id
    private UUID id;

    @Column(name = "cid", length = 10, nullable = false, unique = true)
    private String cid;

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

    @Column(name = "language", length = 5)
    private String language;

    @Column(name = "avatarUrl", length = 255)
    private String avatar_url;

    @Column(name = "gdpr", nullable = false)
    private boolean gdpr;

    @Column(name = "user_agreement", nullable = false)
    private boolean userAgreement;

    @Column(name = "acceptance_year", nullable = false)
    private int acceptanceYear;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
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

    public String getPassword() {
        return password;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
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
                Objects.equals(avatar_url, itUser.avatar_url) &&
                Objects.equals(acceptanceYear, itUser.acceptanceYear) &&
                Objects.equals(createdAt, itUser.createdAt) &&
                Objects.equals(lastModifiedAt, itUser.lastModifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cid, password, nick, firstName, lastName, email, phone, language, avatar_url, gdpr, userAgreement, acceptanceYear, createdAt, lastModifiedAt);
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
                ", language='" + language + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", gdpr=" + gdpr +
                ", userAgreement=" + userAgreement +
                ", acceptanceYear=" + acceptanceYear +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}
