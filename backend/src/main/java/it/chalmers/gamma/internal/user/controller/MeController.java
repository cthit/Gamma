package it.chalmers.gamma.internal.user.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.internal.user.service.MeService;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.GroupPost;
import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/users/me")
public class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

    private final MeService meService;
    private final UserService userService;
    private final MembershipService membershipService;
    private final AuthorityFinder authorityFinder;

    public MeController(MeService meService,
                        UserService userService,
                        MembershipService membershipService,
                        AuthorityFinder authorityFinder) {
        this.meService = meService;
        this.userService = userService;
        this.membershipService = membershipService;
        this.authorityFinder = authorityFinder;
    }

    public record GetMeResponse(@JsonUnwrapped User user,
                                List<GroupPost> groups,
                                List<AuthorityLevelName> authorities) { }

    @GetMapping()
    public GetMeResponse getMe(Principal principal) {
        try {
            User user = extractUser(principal);
            List<GroupPost> groups = this.membershipService.getMembershipsByUser(user.id())
                    .stream()
                    .map(membership -> new GroupPost(membership.post(), membership.group()))
                    .collect(Collectors.toList());
            List<AuthorityLevelName> authorityLevelNames = this.authorityFinder.getGrantedAuthorities(user.id());

            return new GetMeResponse(user, groups, authorityLevelNames);
        } catch (UserService.UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public record EditMeRequest (Nick nick,
                                 FirstName firstName,
                                 LastName lastName,
                                 Email email,
                                 Language language,
                                 int acceptanceYear) { }

    @PutMapping()
    public UserEditedResponse editMe(Principal principal, @RequestBody EditMeRequest request) {
        try {
            User user = extractUser(principal);

            this.userService.update(
                    user.with()
                            .nick(request.nick())
                            .firstName(request.firstName())
                            .lastName(request.lastName())
                            .email(request.email())
                            .language(request.language())
                            .acceptanceYear(Year.of(request.acceptanceYear()))
                            .build()
            );
        } catch (UserService.UserNotFoundException e) {
            e.printStackTrace();
        }

        return new UserEditedResponse();
    }

    @PutMapping("/avatar")
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        try {
            User user = extractUser(principal);
            this.userService.editProfilePicture(user, file);
            return new EditedProfilePictureResponse();
        } catch (UserService.UserNotFoundException e) {
            LOGGER.error("Cannot find the signed in user", e);
            throw new UserNotFoundResponse();
        }
    }

    record ChangeUserPassword(String oldPassword, UnencryptedPassword password) { }

    @PutMapping("/change_password")
    public PasswordChangedResponse changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request) {
        try {
            User user = this.extractUser(principal);

            if (!this.userService.passwordMatches(user.id(), request.oldPassword)) {
                throw new IncorrectCidOrPasswordResponse();
            }

            this.userService.setPassword(user.id(), request.password);
        } catch (UserService.UserNotFoundException e) {
            LOGGER.error("Cannot find the signed in user", e);
            throw new UserNotFoundResponse();
        }
        return new PasswordChangedResponse();
    }

    record DeleteMeRequest (String password) { }

    @DeleteMapping()
    public UserDeletedResponse deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request) {
        try {
            User user = this.extractUser(principal);

            this.meService.tryToDeleteUser(user.id(), request.password);

            return new UserDeletedResponse();
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private User extractUser(Principal principal) throws UserService.UserNotFoundException {
        return this.userService.get(new Cid(principal.getName()));
    }

    private static class EditedProfilePictureResponse extends SuccessResponse { }

    private static class UserEditedResponse extends SuccessResponse { }

    private static class PasswordChangedResponse extends SuccessResponse { }

    private static class UserDeletedResponse extends SuccessResponse { }

    private static class UserNotFoundResponse extends ErrorResponse {
        public UserNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

    private static class IncorrectCidOrPasswordResponse extends ErrorResponse {
        public IncorrectCidOrPasswordResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
