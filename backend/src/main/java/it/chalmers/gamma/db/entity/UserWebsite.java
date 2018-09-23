package it.chalmers.gamma.db.entity;

import javax.persistence.*;
import java.util.UUID;

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

    public UserWebsite(){
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ITUser getItUser() {
        return itUser;
    }

    public void setItUser(ITUser itUser) {
        this.itUser = itUser;
    }

    public WebsiteURL getWebsite() {
        return website;
    }

    public void setWebsite(WebsiteURL website) {
        this.website = website;
    }
}
