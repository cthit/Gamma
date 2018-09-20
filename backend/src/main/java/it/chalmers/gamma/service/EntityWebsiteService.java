package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.repository.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.requests.CreateGroupRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public List<WebsiteView> getWebsitesOrdered(List<WebsiteInterface> websites){
        List<Website> websiteTypes = new ArrayList<>();
        List<WebsiteView> groupedWebsites = new ArrayList<>();

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
                WebsiteView newGroup = new WebsiteView(website.getWebsite().getWebsite());
                newGroup.setUrl(new ArrayList<>());
                newGroup.getUrl().add(website.getWebsite().getUrl());
                groupedWebsites.add(newGroup);
            }
        }
        return groupedWebsites;
    }

    public class WebsiteView{
        private UUID id;
        private String name;
        private String prettyName;
        private List<String> url;

        private WebsiteView(Website website){
            id = website.getId();
            name = website.getName();
            prettyName = website.getPrettyName();
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrettyName() {
            return prettyName;
        }

        public void setPrettyName(String privateName) {
            this.prettyName = privateName;
        }

        public List<String> getUrl() {
            return url;
        }

        public void setUrl(List<String> url) {
            this.url = url;
        }
    }

}
