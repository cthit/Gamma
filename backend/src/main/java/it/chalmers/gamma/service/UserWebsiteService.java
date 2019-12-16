package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.UserWebsite;
import it.chalmers.gamma.db.repository.UserWebsiteRepository;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.website.UserWebsiteDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteInterfaceDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class UserWebsiteService extends EntityWebsiteService {

    private final UserWebsiteRepository repository;
    private final DTOToEntityService dtoToEntityService;
    private final WebsiteURLService websiteURLService;
    private final WebsiteService websiteService;

    public UserWebsiteService(UserWebsiteRepository repository,
                              WebsiteService websiteService,
                              DTOToEntityService dtoToEntityService,
                              WebsiteURLService websiteURLService) {
        super(websiteService);
        this.repository = repository;
        this.dtoToEntityService = dtoToEntityService;
        this.websiteURLService = websiteURLService;
        this.websiteService = websiteService;
    }

    /**
     * adds an associated websiteurl to a user.
     *
     * @param user     the ITUser to handle
     * @param websites all websites that should be added to the user
     */
    public void addWebsiteToUser(ITUserDTO user, List<WebsiteUrlDTO> websites) {
        for (WebsiteUrlDTO website : websites) {
            UserWebsite userWebsite = new UserWebsite();
            userWebsite.setItUser(this.dtoToEntityService.fromDTO(user));
            userWebsite.setWebsite(this.websiteURLService.getWebsiteURL(website));
            this.repository.save(userWebsite);
        }
    }

    public List<UserWebsiteDTO> getAllUserWebsites() {
        return this.repository.findAll().stream().map(UserWebsite::toDTO).collect(Collectors.toList());
    }

    public UserWebsiteDTO getUserWebsiteById(String id) {
        return this.repository.findById(UUID.fromString(id)).map(UserWebsite::toDTO).orElse(null);
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
    public List<WebsiteInterfaceDTO> getWebsites(ITUserDTO user) {
        return this.repository.findAllByItUser(this.dtoToEntityService.fromDTO(user))
                .stream().map(UserWebsite::toDTO).collect(Collectors.toList());
    }

    public UserWebsiteDTO getUserWebsiteByWebsite(WebsiteDTO website) {
        return this.repository.findByWebsite(this.websiteService.getWebsite(website)).map(UserWebsite::toDTO)
                .orElse(null);
    }

    /**
     * deletes all websites connected to a user,
     * IE done before deleting a user to not have dangling tables.
     *
     * @param user the user
     */
    @Transactional
    public void deleteWebsitesConnectedToUser(ITUserDTO user) {
        this.repository.deleteAllByItUser(this.dtoToEntityService.fromDTO(user));
    }

    /**
     * Deletes all websites of a specific type.
     *
     * @param website the type of website to delete
     */
    @Transactional
    public void deleteUserWebsiteByWebsite(WebsiteDTO website) {
        this.repository.deleteAllByWebsite_Website(this.websiteService.getWebsite(website));
    }

}
