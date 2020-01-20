package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import it.chalmers.gamma.requests.CreateWebsiteRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.website.EditedWebsiteResponse;
import it.chalmers.gamma.response.website.WebsiteAddedResponse;
import it.chalmers.gamma.response.website.WebsiteDeletedResponse;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.UserWebsiteService;
import it.chalmers.gamma.service.WebsiteService;
import it.chalmers.gamma.util.InputValidationUtils;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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


    @PostMapping()
    public WebsiteAddedResponse addWebsite(@Valid @RequestBody CreateWebsiteRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        this.websiteService.addPossibleWebsite(request.getName(), request.getPrettyName());
        return new WebsiteAddedResponse();
    }

    @PutMapping("/{id}")
    public EditedWebsiteResponse editWebsite(
            @PathVariable("id") String id,
            @RequestBody CreateWebsiteRequest request) {
        WebsiteDTO website = this.websiteService.getWebsite(id);
        this.websiteService.editWebsite(website, request.getName(), request.getPrettyName());
        return new EditedWebsiteResponse();
    }

    @DeleteMapping("/{id}")
    public WebsiteDeletedResponse deleteWebsite(@PathVariable("id") String id) {
        WebsiteDTO website = this.websiteService.getWebsite(id);
        this.groupWebsiteService.deleteGroupWebsiteByWebsite(website);
        this.userWebsiteService.deleteUserWebsiteByWebsite(website);
        this.websiteService.deleteWebsite(id);
        return new WebsiteDeletedResponse();
    }


}
