package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.repository.WebsiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WebsiteService {
    private WebsiteRepository repository;
    public WebsiteService(WebsiteRepository repository){
        this.repository = repository;
    }

    /**
     * adds a possible website to the database
     * @param name the name of the website
     * @param prettyName the display-name of the website
     */
    public void addPossibleWebsite(String name, String prettyName){
        Website website = new Website();
        website.setPrettyName(prettyName != null ? prettyName : name.toLowerCase());
        website.setName(name);
        repository.save(website);
    }
    public Website getWebsite(String websiteName){
        return repository.findByName(websiteName);
    }
    public void editWebsite(Website website, String name, String prettyName){
        website.setName(name.toLowerCase());
        website.setPrettyName(prettyName != null ? prettyName : name.toLowerCase());
        repository.save(website);
    }
    public void deleteWebsite(String id){
        repository.deleteById(UUID.fromString(id));
    }
    public Website getWebsiteById(String id){
        return repository.findById(UUID.fromString(id)).orElse(null);
    }
    public List<Website> getAllWebsites(){
        return repository.findAll();
    }
}
