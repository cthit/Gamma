package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;
import it.chalmers.gamma.requests.AdminChangePasswordRequest;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.requests.EditITUserRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.user.GetAllITUsersResponse;
import it.chalmers.gamma.response.user.GetAllITUsersResponse.GetAllITUsersResponseObject;
import it.chalmers.gamma.response.user.GetITUserResponse;
import it.chalmers.gamma.response.user.GetITUserResponse.GetITUserResponseObject;
import it.chalmers.gamma.response.user.PasswordChangedResponse;
import it.chalmers.gamma.response.user.UserAlreadyExistsResponse;
import it.chalmers.gamma.response.user.UserCreatedResponse;
import it.chalmers.gamma.response.user.UserDeletedResponse;
import it.chalmers.gamma.response.user.UserEditedResponse;
import it.chalmers.gamma.response.user.UserNotFoundResponse;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.UserWebsiteService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.ExcessiveImports"})
@RestController
@RequestMapping("/admin/users")
public final class UsersAdminController {

    private final ITUserService itUserService;
    private final UserWebsiteService userWebsiteService;


    private final MembershipService membershipService;

    public UsersAdminController(
            ITUserService itUserService,
            UserWebsiteService userWebsiteService,
            MembershipService membershipService) {
        this.itUserService = itUserService;
        this.userWebsiteService = userWebsiteService;

        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}/change_password", method = RequestMethod.PUT)
    public PasswordChangedResponse changePassword(
            @PathVariable("id") String id,
            @Valid @RequestBody AdminChangePasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUserDTO user = this.itUserService.getITUser(id);
        this.itUserService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    //TODO Make sure that the code to add websites to users actually works
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public UserEditedResponse editUser(@PathVariable("id") String id,
                                           @RequestBody EditITUserRequest request) {
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.itUserService.editUser(
                UUID.fromString(id),
                request.getNick(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getLanguage());
        // Below handles adding websites.
        ITUserDTO user = this.itUserService.getITUser(id);
        List<WebsiteUrlDTO> websiteURLs = new ArrayList<>();
        this.userWebsiteService.addWebsiteToUser(user, websiteURLs);
        return new UserEditedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public UserDeletedResponse deleteUser(@PathVariable("id") String id) {
        ITUserDTO user = this.itUserService.getITUser(id);
        this.userWebsiteService.deleteWebsitesConnectedToUser(user);
        this.membershipService.removeAllMemberships(user);
        this.itUserService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public GetITUserResponseObject getUser(@PathVariable("id") String id) {
        ITUserDTO user = this.itUserService.getITUser(id);
        // List<WebsiteUrlDTO> websites = this.userWebsiteService.getWebsitesOrdered(
        //                 this.userWebsiteService.getWebsites(user));
        List<FKITGroupDTO> groups = this.membershipService.getUsersGroupDTO(user);
        return new GetITUserResponse(user, groups, null).toResponseObject();
    }

    @RequestMapping(method = RequestMethod.GET)
    public GetAllITUsersResponseObject getAllUsers() {

        List<ITUserDTO> users = this.itUserService.loadAllUsers();
        List<GetITUserResponse> userResponses = users.stream()
                .map(u -> new GetITUserResponse(u, this.membershipService.getUsersGroupDTO(u), null))
                .collect(Collectors.toList());
        return new GetAllITUsersResponse(userResponses).toResponseObject();
    }

    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @RequestMapping(method = RequestMethod.POST)
    public UserCreatedResponse addUser(
            @Valid @RequestBody AdminViewCreateITUserRequest createITUserRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.itUserService.userExists(createITUserRequest.getCid())) {
            throw new UserAlreadyExistsResponse();
        }
        this.itUserService.createUser(
                createITUserRequest.getNick(),
                createITUserRequest.getFirstName(),
                createITUserRequest.getLastName(),
                createITUserRequest.getCid(),
                Year.of(createITUserRequest.getAcceptanceYear()),
                createITUserRequest.isUserAgreement(),
                createITUserRequest.getEmail(),
                createITUserRequest.getPassword()
        );
        return new UserCreatedResponse();
    }
}
