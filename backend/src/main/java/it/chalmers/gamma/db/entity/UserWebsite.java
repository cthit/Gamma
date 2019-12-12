package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.domain.dto.website.UserWebsiteDTO;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ituser_website")
public class UserWebsite implements WebsiteInterface {

    @Id
    @Column(updatable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "ituser")
    private ITUser itUser;

    @JoinColumn(name = "website")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private WebsiteURL website;

    public UserWebsite() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ITUser getItUser() {
        return this.itUser;
    }

    public void setItUser(ITUser itUser) {
        this.itUser = itUser;
    }

    @Override
    public WebsiteURL getWebsite() {
        return this.website;
    }

    public void setWebsite(WebsiteURL website) {
        this.website = website;
    }

    public UserWebsiteDTO toDTO() {
        return new UserWebsiteDTO(this.id, this.itUser.toDTO(), this.website.toDTO());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserWebsite that = (UserWebsite) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.itUser, that.itUser)
                && Objects.equals(this.website, that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.itUser, this.website);
    }

    @Override
    public String toString() {
        return "UserWebsite{"
                + "id=" + this.id
                + ", itUser=" + this.itUser
                + ", website=" + this.website
                + '}';
    }
}
