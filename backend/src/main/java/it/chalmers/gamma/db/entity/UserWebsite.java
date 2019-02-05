package it.chalmers.gamma.db.entity;

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
}
