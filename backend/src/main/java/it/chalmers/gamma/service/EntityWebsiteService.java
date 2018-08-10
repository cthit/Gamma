package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.requests.CreateGroupRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

}
