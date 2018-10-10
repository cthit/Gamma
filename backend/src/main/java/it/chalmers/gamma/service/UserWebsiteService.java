package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.repository.UserWebsiteRepository;
import it.chalmers.gamma.db.entity.WebsiteInterface;
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

    /**
     * adds an associated websiteurl to a user
     * @param user the ITUser to handle
     * @param websites all websites that should be added to the user
     */
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
    //TODO function bellow
    public void deleteUserWebsite(String id){

    }

    /**
     * gets all websites connected to a user
     * @param user the user to search for connected websites
     * @return all websites connected to a user
     */
    public List<WebsiteInterface> getWebsites(ITUser user){
        List<UserWebsite> userWebsites = repository.findAllByItUser(user);
        return new ArrayList<>(userWebsites);
    }
    public UserWebsite getUserWebsiteByWebsite(Website website){
        return repository.findByWebsite(website);
    }

    /**
     * deletes all websites connected to a user, IE done before deleting a user to not have dangling tables
     * @param user the user
     */
    @Transactional
    public void deleteWebsitesConnectedToUser(ITUser user){
        repository.deleteAllByItUser(user);
    }

    /**
     * Deletes all websites of a specific type
     * @param website the type of website to delete
     */
    @Transactional
    public void deleteUserWebsiteByWebsite(Website website){
        repository.deleteAllByWebsite_Website(website);
    }
}
