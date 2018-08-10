package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.GroupWebsite;
import it.chalmers.gamma.db.entity.UserWebsite;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.requests.AddListOfWhitelistedRequest;
import it.chalmers.gamma.requests.CreateWebsiteRequest;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/websites")
public class WebsiteAdminController {

    private WebsiteService websiteService;
    private GroupWebsiteService groupWebsiteService;
    private UserWebsiteService userWebsiteService;


    public WebsiteAdminController(WebsiteService websiteService, GroupWebsiteService groupWebsiteService, UserWebsiteService userWebsiteService){
        this.websiteService = websiteService;
        this.groupWebsiteService = groupWebsiteService;
        this.userWebsiteService = userWebsiteService;
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addWebsite(@RequestBody CreateWebsiteRequest request){
        if(request.getName() == null){
            return new MissingRequiredFieldResponse("name");
        }
        websiteService.addPossibleWebsite(request.getName(), request.getPrettyName());
        return new WebsiteAddedResponse();
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Website> getWebsite(@PathVariable("id") String id){
        return new GetWebsiteResponse(websiteService.getWebsiteById(id));
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editWebsite(@PathVariable("id") String id, @RequestBody CreateWebsiteRequest request){
        Website website = websiteService.getWebsiteById(id);
        if(website == null){
            return new WebsiteNotFoundResponse();
        }
        websiteService.editWebsite(website, request.getName(), request.getPrettyName());
        return new EditedWebsiteResponse();
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteWebsite(@PathVariable("id") String id){
        groupWebsiteService.deleteGroupWebsiteByWebsite(websiteService.getWebsiteById(id));
        userWebsiteService.deleteUserWebsiteByWebsite(websiteService.getWebsiteById(id));
        websiteService.deleteWebsite(id);
        return new WebsiteDeletedResponse();
    }
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Website>> getAllWebsites(){
        return new GetAllWebsitesResponse(websiteService.getAllWebsites());
    }

}
