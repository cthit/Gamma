package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * object to store websites in ordered way, used by getWebsitesOrdered method.
 */
public class WebsiteView {
    private UUID id;
    private String name;
    private String prettyName;
    private List<String> url;

    WebsiteView(Website website) {
        this.id = website.getId();
        this.name = website.getName();
        this.prettyName = website.getPrettyName();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUrl() {
        return this.url;
    }

    public void setUrl(List<String> url) {
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
        WebsiteView that = (WebsiteView) o;
        return this.id.equals(that.id)
                && this.name.equals(that.name)
                && this.prettyName.equals(that.prettyName)
                && this.url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.prettyName, this.url);
    }

    @Override
    public String toString() {
        return "WebsiteView{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", url=" + this.url
                + '}';
    }
}

