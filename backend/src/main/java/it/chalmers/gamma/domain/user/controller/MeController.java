package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupPost;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.user.controller.request.ChangeUserPassword;
import it.chalmers.gamma.domain.user.controller.request.DeleteMeRequest;
import it.chalmers.gamma.domain.user.controller.request.EditITUserRequest;
import it.chalmers.gamma.domain.user.controller.response.*;
import it.chalmers.gamma.domain.user.data.dto.UserDTO;
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

@RestController
@RequestMapping("/users/me")
public class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

    private final UserService userService;
    private final UserFinder userFinder;
    private final MembershipFinder membershipFinder;
    private final AuthorityFinder authorityFinder;

    public MeController(UserService userService,
                        UserFinder userFinder,
                        MembershipFinder membershipFinder,
                        AuthorityFinder authorityFinder) {
        this.userService = userService;
        this.userFinder = userFinder;
        this.membershipFinder = membershipFinder;
        this.authorityFinder = authorityFinder;
    }

    @GetMapping()
    public GetMeResponse getMe(Principal principal) {
        try {
            UserDTO user = extractUser(principal);
            List<GroupPost> groups = this.membershipFinder.getMembershipsByUser(user.getId())
                    .stream()
                    .map(membership -> new GroupPost(membership.getPost(), membership.getGroup()))
                    .collect(Collectors.toList());
            List<AuthorityLevelName> authorityLevelNames = this.authorityFinder.getGrantedAuthorities(user.getId());

            return new GetMeResponse(user, groups, authorityLevelNames);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Signed in user doesn't exist, maybe deleted?", e);
            throw new UserNotFoundResponse();
        }
    }

    @PutMapping()
    public UserEditedResponse editMe(Principal principal, @RequestBody EditITUserRequest request) {
        try {
            UserDTO user = extractUser(principal);

            this.userService.update(
                    new UserDTO.UserDTOBuilder()
                            .from(user)
                            .nick(request.nick)
                            .firstName(request.firstName)
                            .lastName(request.lastName)
                            .email(request.email)
                            .language(request.language)
                            .acceptanceYear(Year.of(request.acceptanceYear))
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

    @PutMapping("/change_password")
    public PasswordChangedResponse changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request) {
        UserDTO user = null;
        try {
            user = this.extractUser(principal);

            if (!this.userService.passwordMatches(user, request.getOldPassword())) {
                throw new IncorrectCidOrPasswordResponse();
            }

            this.userService.setPassword(user.getId(), request.getPassword());
        } catch (EntityNotFoundException e) {
            LOGGER.error("Cannot find the signed in user", e);
            throw new UserNotFoundResponse();
        }
        return new PasswordChangedResponse();
    }

    @DeleteMapping()
    public UserDeletedResponse deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request) {
        try {
            UserDTO user = this.extractUser(principal);

            if (!this.userService.passwordMatches(user, request.getPassword())) {
                throw new IncorrectCidOrPasswordResponse();
            }

            this.userService.delete(user.getId());

            return new UserDeletedResponse();
        } catch (EntityNotFoundException e) {
            LOGGER.error("Can't find user, already deleted?", e);
            throw new UserNotFoundResponse();
        }
    }

    private UserDTO extractUser(Principal principal) throws EntityNotFoundException {
        return this.userFinder.get(new Cid(principal.getName()));
    }

}
