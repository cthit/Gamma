package it.chalmers.delta.views;

import it.chalmers.delta.db.entity.Website;

import java.util.List;
import java.util.UUID;

/**
 * object to store websites in ordered way, used by getWebsitesOrdered method.
 */
public class WebsiteView {
    private UUID id;
    private String name;
    private final String prettyName;
    private List<String> url;

    public WebsiteView(Website website) {
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

    public String getPrettyName() {
        return this.prettyName;
    }
}

