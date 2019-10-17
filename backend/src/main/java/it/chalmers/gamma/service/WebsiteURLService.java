package it.chalmers.delta.service;

import it.chalmers.delta.db.entity.Website;
import it.chalmers.delta.db.entity.WebsiteURL;
import it.chalmers.delta.db.repository.WebsiteURLRepository;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

//TODO this class might be unnecessary because of changes in how websites work
@SuppressWarnings("PMD.UnusedPrivateMethod")
@Service
public class WebsiteURLService {

    private final WebsiteURLRepository repository;

    public WebsiteURLService(WebsiteURLRepository repository) {
        this.repository = repository;
    }

    private void addWebsite(Website website, String url) {
        WebsiteURL websiteURL = new WebsiteURL();
        websiteURL.setWebsite(website);
        websiteURL.setUrl(url);
        this.repository.save(websiteURL);
    }

    public List<WebsiteURL> getAllWebsites() {
        return this.repository.findAll();
    }

    public WebsiteURL getWebsiteURLById(String id) {
        return this.repository.findById(UUID.fromString(id)).orElse(null);
    }

    public void deleteWebsite(String id) {
        this.repository.deleteById(UUID.fromString(id));
    }

    public void editWebsite(WebsiteURL websiteURL, Website website, String url) {
        websiteURL.setWebsite(website);
        websiteURL.setUrl(url);
        this.repository.save(websiteURL);
    }

    @Transactional
    public void deleteAllWebsites(Website website) {
        this.repository.deleteAllByWebsite(website);
    }

}
