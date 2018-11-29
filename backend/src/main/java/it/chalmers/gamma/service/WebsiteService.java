package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.repository.WebsiteRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public final class WebsiteService {

    private final WebsiteRepository repository;

    private WebsiteService(WebsiteRepository repository) {
        this.repository = repository;
    }

    /**
     * adds a possible website to the database.
     *
     * @param name       the name of the website
     * @param prettyName the display-name of the website
     */
    public void addPossibleWebsite(String name, String prettyName) {
        Website website = new Website();
        website.setPrettyName(prettyName == null ? name.toLowerCase() : prettyName);
        website.setName(name);
        this.repository.save(website);
    }

    public Website getWebsite(String websiteName) {
        return this.repository.findByName(websiteName);
    }

    public void editWebsite(Website website, String name, String prettyName) {
        website.setName(name.toLowerCase());
        website.setPrettyName(prettyName == null ? name.toLowerCase() : prettyName);
        this.repository.save(website);
    }

    public void deleteWebsite(String id) {
        this.repository.deleteById(UUID.fromString(id));
    }

    public Website getWebsiteById(String id) {
        return this.repository.findById(UUID.fromString(id)).orElse(null);
    }

    public List<Website> getAllWebsites() {
        return this.repository.findAll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebsiteService that = (WebsiteService) o;
        return this.repository.equals(that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.repository);
    }

    @Override
    public String toString() {
        return "WebsiteService{"
            + "repository=" + this.repository
            + '}';
    }
}
