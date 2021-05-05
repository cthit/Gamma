package it.chalmers.gamma.domain.user.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.user.service.MeService;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupPost;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.user.service.UserDTO;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

import static it.chalmers.gamma.domain.user.controller.UserStatusResponses.*;

@RestController
@RequestMapping("/users/me")
public class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

    private final MeService meService;
    private final UserService userService;
    private final UserFinder userFinder;
    private final MembershipFinder membershipFinder;
    private final AuthorityFinder authorityFinder;

    public MeController(MeService meService,
                        UserService userService,
                        UserFinder userFinder,
                        MembershipFinder membershipFinder,
                        AuthorityFinder authorityFinder) {
        this.meService = meService;
        this.userService = userService;
        this.userFinder = userFinder;
        this.membershipFinder = membershipFinder;
        this.authorityFinder = authorityFinder;
    }

    public record GetMeResponse(@JsonUnwrapped UserDTO user,
                                List<GroupPost> groups,
                                List<AuthorityLevelName> authorities) { }

    @GetMapping()
    public GetMeResponse getMe(Principal principal) {
        try {
            UserDTO user = extractUser(principal);
            List<GroupPost> groups = this.membershipFinder.getMembershipsByUser(user.id())
                    .stream()
                    .map(membership -> new GroupPost(membership.post(), membership.group()))
                    .collect(Collectors.toList());
            List<AuthorityLevelName> authorityLevelNames = this.authorityFinder.getGrantedAuthorities(user.id());

            return new GetMeResponse(user, groups, authorityLevelNames);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Signed in user doesn't exist, maybe deleted?", e);
            throw new UserNotFoundResponse();
        }
    }

    public record EditMeRequest (String nick,
                                   String firstName,
                                   String lastName,
                                   Email email,
                                   Language language,
                                   int acceptanceYear) { }

    @PutMapping()
    public UserEditedResponse editMe(Principal principal, @RequestBody EditMeRequest request) {
        try {
            UserDTO user = extractUser(principal);

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
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        return new UserEditedResponse();
    }

    @PutMapping("/avatar")
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        try {
            UserDTO user = extractUser(principal);
            this.userService.editProfilePicture(user, file);
            return new EditedProfilePictureResponse();
        } catch (EntityNotFoundException e) {
            LOGGER.error("Cannot find the signed in user", e);
            throw new UserNotFoundResponse();
        }
    }

    record ChangeUserPassword(String oldPassword, String password) { }

    @PutMapping("/change_password")
    public PasswordChangedResponse changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request) {
        try {
            UserDTO user = this.extractUser(principal);

            if (!this.userService.passwordMatches(user.id(), request.oldPassword())) {
                throw new IncorrectCidOrPasswordResponse();
            }

            this.userService.setPassword(user.id(), request.password);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Cannot find the signed in user", e);
            throw new UserNotFoundResponse();
        }
        return new PasswordChangedResponse();
    }

    record DeleteMeRequest (String password) { }

    @DeleteMapping()
    public UserDeletedResponse deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request) {
        try {
            UserDTO user = this.extractUser(principal);

            this.meService.tryToDeleteUser(user.id(), request.password);

            return new UserDeletedResponse();
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private UserDTO extractUser(Principal principal) throws EntityNotFoundException {
        return this.userFinder.get(new Cid(principal.getName()));
    }

}
