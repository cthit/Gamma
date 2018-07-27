package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.repository.WebsiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WebsiteService {
    private WebsiteRepository repository;
    public WebsiteService(WebsiteRepository repository){
        this.repository = repository;
    }

    public void addPossibleWebsite(String websiteName){
        Website website = new Website();
        website.setName(websiteName);
        repository.save(website);
    }
    public Website getWebsite(String id){
        Optional<Website> website = repository.findById(UUID.fromString(id));
        return website.orElse(null);
    }
    public void editWebsite(Website website, String newWebsite){
        website.setName(newWebsite);
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
