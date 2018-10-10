package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteInterface;
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

    /**
     * creates a list of websiteURLs from a list of websiteinfo and makes sure that no duplicates are made
     * @param websiteInfos a list of websiteinfo, meaning a type of website and a url
     * @param entityWebsites a list of objects that extends EntityWebsites IE either GroupWebsites or UserWebsites
     * @return a list of websiteURLs created from the added website info
     */
    public List<WebsiteURL> addWebsiteToEntity(List<CreateGroupRequest.WebsiteInfo> websiteInfos, List<WebsiteInterface> entityWebsites){
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        for(CreateGroupRequest.WebsiteInfo websiteInfo : websiteInfos){
            boolean websiteExists = false;
            Website website = websiteService.getWebsite(websiteInfo.getWebsite());
            WebsiteURL websiteURL;
            for(WebsiteInterface duplicateCheck : entityWebsites){      //makes sure that the URL has not been added to the entity already
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

    /**
     * gets all websites ordered after type I.E all facebook pages are subpages of type facebook
     * @param websites a list of websites to be ordered
     * @return a list of websites in an ordered fashion
     */
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

    /**
     * object to store websites in ordered way, used by getWebsitesOrdered method
     */
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
