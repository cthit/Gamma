package it.chalmers.delta.controller.admin;

import it.chalmers.delta.db.entity.Website;
import it.chalmers.delta.requests.CreateWebsiteRequest;
import it.chalmers.delta.response.EditedWebsiteResponse;
import it.chalmers.delta.response.GetAllWebsitesResponse;
import it.chalmers.delta.response.GetWebsiteResponse;
import it.chalmers.delta.response.InputValidationFailedResponse;
import it.chalmers.delta.response.WebsiteAddedResponse;
import it.chalmers.delta.response.WebsiteDeletedResponse;
import it.chalmers.delta.response.WebsiteNotFoundResponse;
import it.chalmers.delta.service.GroupWebsiteService;
import it.chalmers.delta.service.UserWebsiteService;
import it.chalmers.delta.service.WebsiteService;
import it.chalmers.delta.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RestController
@RequestMapping("/admin/websites")
public final class WebsiteAdminController {

    private final WebsiteService websiteService;
    private final GroupWebsiteService groupWebsiteService;
    private final UserWebsiteService userWebsiteService;

    public WebsiteAdminController(
            WebsiteService websiteService,
            GroupWebsiteService groupWebsiteService,
            UserWebsiteService userWebsiteService) {
        this.websiteService = websiteService;
        this.groupWebsiteService = groupWebsiteService;
        this.userWebsiteService = userWebsiteService;
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addWebsite(@Valid @RequestBody CreateWebsiteRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        this.websiteService.addPossibleWebsite(request.getName(), request.getPrettyName());
        return new WebsiteAddedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Website> getWebsite(@PathVariable("id") String id) {
        Website website = this.websiteService.getWebsiteById(id);
        if (website == null) {
            throw new WebsiteNotFoundResponse();
        }
        return new GetWebsiteResponse(website);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editWebsite(
            @PathVariable("id") String id,
            @RequestBody CreateWebsiteRequest request) {
        Website website = this.websiteService.getWebsiteById(id);
        if (website == null) {
            throw new WebsiteNotFoundResponse();
        }
        this.websiteService.editWebsite(website, request.getName(), request.getPrettyName());
        return new EditedWebsiteResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteWebsite(@PathVariable("id") String id) {
        if (!this.websiteService.websiteExists(UUID.fromString(id))) {
            throw new WebsiteNotFoundResponse();
        }
        this.groupWebsiteService.deleteGroupWebsiteByWebsite(this.websiteService.getWebsiteById(id));
        this.userWebsiteService.deleteUserWebsiteByWebsite(this.websiteService.getWebsiteById(id));
        this.websiteService.deleteWebsite(id);
        return new WebsiteDeletedResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Website>> getAllWebsites() {
        return new GetAllWebsitesResponse(this.websiteService.getAllWebsites());
    }

}
