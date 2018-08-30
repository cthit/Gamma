package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.repository.UserWebsiteRepository;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserWebsiteService extends EntityWebsiteService{

    private UserWebsiteRepository repository;

    public UserWebsiteService(UserWebsiteRepository repository, WebsiteService websiteService) {
        super(websiteService);
        this.repository = repository;
    }

    public void addWebsiteToUser(ITUser user, List<WebsiteURL> websites){
        for(WebsiteURL website : websites) {
            UserWebsite userWebsite = new UserWebsite();
            userWebsite.setItUser(user);
            userWebsite.setWebsite(website);
            repository.save(userWebsite);
        }
    }
    public List<UserWebsite> getAllUserWebsites(){
        return repository.findAll();
    }

    public UserWebsite getUserWebsiteById(String id){
        return repository.findById(UUID.fromString(id)).orElse(null);
    }
    public void deleteUserWebsite(String id){

    }
    public List<WebsiteInterface> getWebsites(ITUser user){
        List<UserWebsite> userWebsites = repository.findAllByItUser(user);
        return new ArrayList<>(userWebsites);
    }
    public UserWebsite getUserWebsiteByWebsite(Website website){
        return repository.findByWebsite(website);
    }
    @Transactional
    public void deleteWebsitesConnectedToGroup(ITUser user){
        repository.deleteAllByItUser(user);
    }
    @Transactional
    public void deleteUserWebsiteByWebsite(Website website){
        repository.deleteAllByWebsite_Website(website);
    }
}
