package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.repository.UserWebsiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserWebsiteService {

    private UserWebsiteRepository repository;

    public UserWebsiteService(UserWebsiteRepository repository){
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
    public List<UserWebsite> getWebsites(ITUser user){
        return repository.findAllByItUser(user);
    }
    public UserWebsite getUserWebsiteByWebsite(Website website){
        return repository.findByWebsite(website);
    }

}
