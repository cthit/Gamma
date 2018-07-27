package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "website_url")
public class WebsiteURL {
    @Id
    @Column(name = "id", updatable = false)
    @JsonIgnore
    private UUID id;

    @JoinColumn(name = "website")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Website website;

    @Column(name = "url")
    private String url;


    public WebsiteURL(){
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    @JsonIgnore
    public Website getWebsite() {
        return website;
    }

    public String getWebsiteName(){
        return website.getName();
    }

    public void setWebsite(Website website) {
        this.website = website;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebsiteURL that = (WebsiteURL) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(website, that.website) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, website, url);
    }

    @Override
    public String toString() {
        return "WebsiteURL{" +
                "id=" + id +
                ", website=" + website +
                ", url='" + url + '\'' +
                '}';
    }
}
