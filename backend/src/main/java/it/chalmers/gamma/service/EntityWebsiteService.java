package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.requests.CreateGroupRequest;

import java.util.ArrayList;
import java.util.List;

import it.chalmers.gamma.views.WebsiteView;
import org.springframework.stereotype.Service;

@Service
public class EntityWebsiteService {

    private final WebsiteService websiteService;

    protected EntityWebsiteService(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * creates a list of websiteURLs from a list of websiteinfo
     * and makes sure that no duplicates are made.
     *
     * @param websiteInfos   a list of websiteinfo, meaning
     *                       a type of website and a url
     * @param entityWebsites a list of objects that extends
     *                       EntityWebsites IE either GroupWebsites or UserWebsites
     * @return a list of websiteURLs created from the added website info
     */
    public List<WebsiteURL> addWebsiteToEntity(List<CreateGroupRequest.WebsiteInfo> websiteInfos,
                                               List<WebsiteInterface> entityWebsites) {
        if(entityWebsites == null || websiteInfos == null){
            return null;
        }
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        for (CreateGroupRequest.WebsiteInfo websiteInfo : websiteInfos) {
            boolean websiteExists = false;
            Website website = this.websiteService.getWebsite(websiteInfo.getWebsite());
            WebsiteURL websiteURL;

            //makes sure that the URL has not been added to the entity already.
            for (WebsiteInterface duplicateCheck : entityWebsites) {
                if (duplicateCheck.getWebsite().getUrl().equals(websiteInfo.getUrl())) {
                    websiteExists = true;
                    break;
                }
            }
            if (!websiteExists) {
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
     *
     * @param websites a list of websites to be ordered
     * @return a list of websites in an ordered fashion
     */
    public List<WebsiteView> getWebsitesOrdered(List<WebsiteInterface> websites) {
        List<Website> websiteTypes = new ArrayList<>();
        List<WebsiteView> groupedWebsites = new ArrayList<>();

        //loops through all websites added to group.
        for (WebsiteInterface website : websites) {
            boolean websiteFound = false;

            //loops through all added website types.
            for (int y = 0; y < websiteTypes.size(); y++) {

                // checks if the website has been added to found types.
                if (websiteTypes.get(y).equals(website.getWebsite().getWebsite())) {

                    // if website has been found before the url
                    // is added to a list of websites connected to that.
                    groupedWebsites.get(y).getUrl().add(website.getWebsite().getUrl());
                    websiteFound = true;
                }
            }
            if (!websiteFound) {

                // if the websitetype is not found, it is added.
                websiteTypes.add(website.getWebsite().getWebsite());
                WebsiteView newGroup = new WebsiteView(website.getWebsite().getWebsite());
                newGroup.setUrl(new ArrayList<>());
                newGroup.getUrl().add(website.getWebsite().getUrl());
                groupedWebsites.add(newGroup);
            }
        }
        return groupedWebsites;
    }

}
