package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "website_url")
public class WebsiteURL {
    @Id
    @Column(name = "id", updatable = false)
    @JsonIgnore
    private UUID id;

    @JoinColumn(name = "website")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private Website website;

    @Column(name = "url")
    private String url;


    public WebsiteURL() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonIgnore
    public Website getWebsite() {
        return this.website;
    }

    public String getWebsiteName() {
        return this.website.getName();
    }

    public void setWebsite(Website website) {
        this.website = website;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebsiteURL that = (WebsiteURL) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.website, that.website)
                && Objects.equals(this.url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.website, this.url);
    }

    @Override
    public String toString() {
        return "WebsiteURL{"
                + "id=" + this.id
                + ", website=" + this.website
                + ", url='" + this.url + '\''
                + '}';
    }
}
