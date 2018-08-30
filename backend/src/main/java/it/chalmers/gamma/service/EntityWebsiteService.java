package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.GroupWebsite;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.requests.CreateGroupRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EntityWebsiteService{
    private WebsiteService websiteService;
    public EntityWebsiteService(WebsiteService websiteService){
        this.websiteService = websiteService;
    }


    public List<WebsiteURL> addWebsiteToEntity(List<CreateGroupRequest.WebsiteInfo> websiteInfos, List<WebsiteInterface> entityWebsite){
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        for(CreateGroupRequest.WebsiteInfo websiteInfo : websiteInfos){
        boolean websiteExists = false;
        Website website = websiteService.getWebsite(websiteInfo.getWebsite());
        WebsiteURL websiteURL;
        for(WebsiteInterface duplicateCheck : entityWebsite){
            if(duplicateCheck.getWebsite().getUrl().equals(websiteInfo.getUrl())) {
                websiteExists = true;
                break;
            }
        }
        if(!websiteExists) {
            websiteURL = new WebsiteURL();
            websiteURL.setWebsite(website);
            websiteURL.setUrl(websiteInfo.getUrl());
            websiteURLs.add(websiteURL);
        }
    }
        return websiteURLs;
    }
    public List<Website.WebsiteView> getWebsitesOrdered(List<WebsiteInterface> websites){
        String[] properties = {"id", "name", "prettyName"};
        List<String> props = new ArrayList<>(Arrays.asList(properties));
        List<Website> websiteTypes = new ArrayList<>();
        List<Website.WebsiteView> groupedWebsites = new ArrayList<>();

        for(WebsiteInterface website : websites){       //loops through all websites added to group.
            boolean websiteFound = false;
            for(int y = 0; y < websiteTypes.size(); y++){   //loops through all added website types.
                if(websiteTypes.get(y).equals(website.getWebsite().getWebsite())){  // checks if the website has been added to found types.
                    groupedWebsites.get(y).getUrl().add(website.getWebsite().getUrl()); // if website has been found before the url is added to a list of websites connected to that.
                    websiteFound = true;
                }
            }
            if(!websiteFound) {
                websiteTypes.add(website.getWebsite().getWebsite());    // if the websitetype is not found, it is added.
                Website.WebsiteView newGroup = website.getWebsite().getWebsite().getView(props);
                newGroup.setUrl(new ArrayList<>());
                newGroup.getUrl().add(website.getWebsite().getUrl());
                groupedWebsites.add(newGroup);
            }
        }
        System.out.println(groupedWebsites);
        return groupedWebsites;
    }

}
