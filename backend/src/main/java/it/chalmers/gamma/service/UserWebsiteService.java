package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.UserWebsite;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.repository.UserWebsiteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class UserWebsiteService extends EntityWebsiteService {

    private final UserWebsiteRepository repository;

    public UserWebsiteService(UserWebsiteRepository repository, WebsiteService websiteService) {
        super(websiteService);
        this.repository = repository;
    }

    /**
     * adds an associated websiteurl to a user.
     *
     * @param user     the ITUser to handle
     * @param websites all websites that should be added to the user
     */
    public void addWebsiteToUser(ITUser user, List<WebsiteURL> websites) {
        for (WebsiteURL website : websites) {
            UserWebsite userWebsite = new UserWebsite();
            userWebsite.setItUser(user);
            userWebsite.setWebsite(website);
            this.repository.save(userWebsite);
        }
    }

    public List<UserWebsite> getAllUserWebsites() {
        return this.repository.findAll();
    }

    public UserWebsite getUserWebsiteById(String id) {
        return this.repository.findById(UUID.fromString(id)).orElse(null);
    }

    //TODO function bellow
    public void deleteUserWebsite(String id) {

    }

    /**
     * gets all websites connected to a user.
     *
     * @param user the user to search for connected websites
     * @return all websites connected to a user
     */
    public List<WebsiteInterface> getWebsites(ITUser user) {
        List<UserWebsite> userWebsites = this.repository.findAllByItUser(user);
        return new ArrayList<>(userWebsites);
    }

    public UserWebsite getUserWebsiteByWebsite(Website website) {
        return this.repository.findByWebsite(website);
    }

    /**
     * deletes all websites connected to a user,
     * IE done before deleting a user to not have dangling tables.
     *
     * @param user the user
     */
    @Transactional
    public void deleteWebsitesConnectedToUser(ITUser user) {
        this.repository.deleteAllByItUser(user);
    }

    /**
     * Deletes all websites of a specific type.
     *
     * @param website the type of website to delete
     */
    @Transactional
    public void deleteUserWebsiteByWebsite(Website website) {
        this.repository.deleteAllByWebsite_Website(website);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserWebsiteService that = (UserWebsiteService) o;
        return this.repository.equals(that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.repository);
    }

    @Override
    public String toString() {
        return "UserWebsiteService{"
            + "repository=" + this.repository
            + '}';
    }
}
