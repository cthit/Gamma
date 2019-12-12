package it.chalmers.gamma.service;

import org.springframework.stereotype.Service;

@Service

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField", "PMD.CommentSize"})
public class EntityWebsiteService {

    private final WebsiteService websiteService;

    protected EntityWebsiteService(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * gets all websites ordered after type I.E all facebook pages are subpages of type facebook
     *
     * @param websites a list of websites to be ordered
     * @return a list of websites in an ordered fashion
     */

    // TODO I DONT THINK THIS FUNCTION ACCTUALLY DOES ANYTHING USEFULL
    // Began rewriting it, but I'm unsure why I created it, leaving it for now.
   /* public List<WebsiteDTO> getWebsitesOrdered(List<WebsiteInterfaceDTO> websites) {
        List<WebsiteDTO> groupedWebsites = new ArrayList<>();
        // gets a list of all websiteTypes that an entity has
        List<WebsiteDTO> websiteTypes = websites.stream().map(w -> w.getWebsiteURL()
                .getWebsiteDTO()).distinct().collect(Collectors.toList());

        //loops through all websites added to group.
        for (WebsiteInterfaceDTO website : websites) {
            boolean websiteFound = false;

            //loops through all added website types.
            for (int y = 0; y < websiteTypes.size(); y++) {

                // checks if the website has been added to found types.
                if (websiteTypes.get(y).equals(website.getWebsiteURL())) {

                    // if website has been found before the url
                    // is added to a list of websites connected to that.
                    groupedWebsites.get(y).getUrl().add(website.getWebsiteURL().getUrl());
                    websiteFound = true;
                }
            }
            if (!websiteFound) {

                // if the websitetype is not found, it is added.
                websiteTypes.add(website.getWebsiteURL().getWebsite());
                WebsiteDTO newGroup = new WebsiteDTO(website.getWebsiteURL().getWebsite());
                newGroup.setUrl(new ArrayList<>());
                newGroup.getUrl().add(website.getWebsiteURL().getUrl());
                groupedWebsites.add(newGroup);
            }
        }
        return groupedWebsites;
    }
*/
}
