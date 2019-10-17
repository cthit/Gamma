package it.chalmers.delta.service;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.GroupWebsite;
import it.chalmers.delta.db.entity.Website;
import it.chalmers.delta.db.entity.WebsiteInterface;
import it.chalmers.delta.db.entity.WebsiteURL;
import it.chalmers.delta.db.repository.GroupWebsiteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/*
 * @ł€®®þþ←↓→œþªßðđŋħ̡ĸłøæ«»©“”nµ̣ΩŁ¢®Þ¥↑↑ıŒÞ§ÐªŊĦ̛&ŁØ<>©‘’Nº
 * Type this in the url for a secret.... If I don't forget.... in that case f me. right
 * Nobody will ever know this is here cause no-one will ever look at this code.
 */

@Service
public class GroupWebsiteService extends EntityWebsiteService {

    private final GroupWebsiteRepository repository;

    public GroupWebsiteService(GroupWebsiteRepository repository, WebsiteService websiteService) {
        super(websiteService);
        this.repository = repository;
    }

    public void addGroupWebsites(FKITGroup group, List<WebsiteURL> websiteURLs) throws DataIntegrityViolationException {
        if (websiteURLs == null || group == null) {
            return;
        }
        boolean error = false;
        for (WebsiteURL websiteURL : websiteURLs) {
            if (websiteURL.getWebsite() == null || websiteURL.getUrl() == null) {
                error = true;
                continue;
            }
            GroupWebsite groupWebsite = new GroupWebsite();
            groupWebsite.setGroup(group);
            groupWebsite.setWebsite(websiteURL);
            this.repository.save(groupWebsite);
        }
        if (error) {
            throw new DataIntegrityViolationException("A SQL Constraint was violated");
        }
    }

    @Transactional
    public void deleteGroupWebsiteByWebsite(Website website) {
        this.repository.deleteAllByWebsite_Website(website);
    }

    public List<GroupWebsite> getAllGroupWebsites() {
        return this.repository.findAll();
    }

    public GroupWebsite getGroupWebsiteById(String id) {
        return this.repository.findById(UUID.fromString(id)).orElse(null);
    }

    public void deleteGroupWebsite(String id) {

    }

    public List<WebsiteInterface> getWebsites(FKITGroup group) {
        List<GroupWebsite> groupWebsites = this.repository.findAllByGroup(group);
        return new ArrayList<>(groupWebsites);
    }

    public GroupWebsite getGroupWebsiteByWebsite(Website website) {
        return this.repository.findByWebsite_Website(website);
    }

    @Transactional
    public void deleteWebsitesConnectedToGroup(FKITGroup group) {
        this.repository.deleteAllByGroup(group);
    }

}
