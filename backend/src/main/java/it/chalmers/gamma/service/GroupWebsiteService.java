package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.GroupWebsite;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.repository.GroupWebsiteRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

/*
 * @ł€®®þþ←↓→œþªßðđŋħ̡ĸłøæ«»©“”nµ̣ΩŁ¢®Þ¥↑↑ıŒÞ§ÐªŊĦ̛&ŁØ<>©‘’Nº
 * Type this in the url for a secret.... If I don't forget.... in that case f me. right
 * Nobody will ever know this is here cause no-one will ever look at this code.
 */

@Service
public class GroupWebsiteService {

    private GroupWebsiteRepository repository;

    public GroupWebsiteService(GroupWebsiteRepository repository){
        this.repository = repository;
    }

    public void addGroupWebsites(FKITGroup group, List<WebsiteURL> websiteURLs){
        for(WebsiteURL websiteURL : websiteURLs) {
            GroupWebsite groupWebsite = new GroupWebsite();
            groupWebsite.setGroup(group);
            groupWebsite.setWebsite(websiteURL);
            repository.save(groupWebsite);
        }
    }
    @Transactional
    public void deleteGroupWebsiteByWebsite(Website website){
        repository.deleteAllByWebsite_Website(website);
    }
    public List<GroupWebsite> getAllGroupWebsites(){
        return repository.findAll();
    }

    public GroupWebsite getGroupWebsiteById(String id){
        return repository.findById(UUID.fromString(id)).orElse(null);
    }
    public void deleteGroupWebsite(String id){

    }
    public List<GroupWebsite> getWebsites(FKITGroup group){
        return repository.findAllByGroup(group);
    }

    public GroupWebsite getGroupWebsiteByWebsite(Website website){
        return repository.findByWebsite_Website(website);
    }
    @Transactional
    public void deleteWebsitesConnectedToGroup(FKITGroup group){
        repository.deleteAllByGroup(group);
    }

}
