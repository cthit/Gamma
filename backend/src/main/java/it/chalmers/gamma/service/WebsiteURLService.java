package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.repository.WebsiteURLRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;


@Service
public class WebsiteURLService {        //TODO this class might be unnecessary because of changes in how websites work
    private WebsiteURLRepository repository;
    WebsiteURLService(WebsiteURLRepository repository){
        this.repository = repository;
    }
    public void addWebsite(Website website, String url){
        WebsiteURL websiteURL = new WebsiteURL();
        websiteURL.setWebsite(website);
        websiteURL.setUrl(url);
        repository.save(websiteURL);
    }
    public List<WebsiteURL> getAllWebsites(){
        return repository.findAll();
    }
    public WebsiteURL getWebsiteURLById(String id){
        return repository.findById(UUID.fromString(id)).orElse(null);
    }
    public void deleteWebsite(String id){
        repository.deleteById(UUID.fromString(id));
    }
    public void editWebsite(WebsiteURL websiteURL, Website website, String url){
        websiteURL.setWebsite(website);
        websiteURL.setUrl(url);
        repository.save(websiteURL);
    }
    @Transactional
    public void deleteAllWebsites(Website website){
        repository.deleteAllByWebsite(website);
    }
}
