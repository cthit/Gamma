package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.requests.CreateGroupRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class EntityWebsiteService {
    private WebsiteService websiteService;

    public EntityWebsiteService(WebsiteService websiteService) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityWebsiteService that = (EntityWebsiteService) o;
        return this.websiteService.equals(that.websiteService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.websiteService);
    }

    @Override
    public String toString() {
        return "EntityWebsiteService{"
            + "websiteService=" + this.websiteService
            + '}';
    }

    /**
     * object to store websites in ordered way, used by getWebsitesOrdered method.
     */
    public class WebsiteView {
        private UUID id;
        private String name;
        private String prettyName;
        private List<String> url;

        private WebsiteView(Website website) {
            this.id = website.getId();
            this.name = website.getName();
            this.prettyName = website.getPrettyName();
        }

        public UUID getId() {
            return this.id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrettyName() {
            return this.prettyName;
        }

        public void setPrettyName(String privateName) {
            this.prettyName = privateName;
        }

        public List<String> getUrl() {
            return this.url;
        }

        public void setUrl(List<String> url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WebsiteView that = (WebsiteView) o;
            return this.id.equals(that.id)
                && this.name.equals(that.name)
                && this.prettyName.equals(that.prettyName)
                && this.url.equals(that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.id, this.name, this.prettyName, this.url);
        }

        @Override
        public String toString() {
            return "WebsiteView{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", url=" + this.url
                + '}';
        }
    }

}
