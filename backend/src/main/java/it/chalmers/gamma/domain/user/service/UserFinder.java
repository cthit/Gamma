package it.chalmers.gamma.domain.user.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.user.data.User;
import it.chalmers.gamma.domain.user.data.UserRepository;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import it.chalmers.gamma.domain.user.data.UserRestrictedWithGroupsDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserFinder {

    private final MembershipFinder membershipFinder;
    private final UserRepository userRepository;

    public UserFinder(MembershipFinder membershipFinder, UserRepository userRepository) {
        this.membershipFinder = membershipFinder;
        this.userRepository = userRepository;
    }

    public boolean userExists(Cid cid) {
        return this.userRepository.existsByCid(cid.value);
    }

    public boolean userExists(UUID id) {
        return this.userRepository.existsById(id);
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<UserRestrictedDTO> getUsersRestricted() {
        return getUsers()
                .stream()
                .map(UserRestrictedDTO::new)
                .collect(Collectors.toList());
    }

    public List<UserRestrictedWithGroupsDTO> getUsersWithMembership() {
        return getUsers()
                .stream()
                .map(user -> {
                    try {
                        return this.toWithGroups(user);
                    } catch (UserNotFoundException ignored) {
                        //ignored since we're going directly from every user
                        return new UserRestrictedWithGroupsDTO(new UserRestrictedDTO(user), new ArrayList<>());
                    }
                })
                .collect(Collectors.toList());
    }

    public UserRestrictedWithGroupsDTO getUserWithMemberships(Cid cid) throws UserNotFoundException {
        return this.toWithGroups(this.getUser(cid));
    }

    public UserRestrictedWithGroupsDTO getUserWithMemberships(UUID id) throws UserNotFoundException {
        return this.toWithGroups(this.getUser(id));
    }

    private UserRestrictedWithGroupsDTO toWithGroups(UserDTO user) throws UserNotFoundException {
        return this.toWithGroups(new UserRestrictedDTO(user));
    }

    private UserRestrictedWithGroupsDTO toWithGroups(UserRestrictedDTO user) throws UserNotFoundException {
        return new UserRestrictedWithGroupsDTO(user,
                this.membershipFinder.getMembershipsByUser(user.getId())
                        .stream()
                        .map(membership -> new UserRestrictedWithGroupsDTO.UserGroup(membership.getPost(), membership.getGroup()))
                        .collect(Collectors.toList()));
    }

    public UserDTO getUser(Email email) throws UserNotFoundException {
        return this.toDTO(getUserEntity(email));
    }

    public UserDTO getUser(Cid cid) throws UserNotFoundException {
        return this.toDTO(getUserEntity(cid));
    }

    public UserDTO getUser(UUID id) throws UserNotFoundException {
        return this.toDTO(getUserEntity(id));
    }

    protected User getUserEntity(Cid cid) throws UserNotFoundException {
        return this.userRepository.findByCid(cid.value)
                .orElseThrow(UserNotFoundException::new);
    }

    protected User getUserEntity(Email email) throws UserNotFoundException {
        return this.userRepository.findByEmail(email.value)
                .orElseThrow(UserNotFoundException::new);
    }

    protected User getUserEntity(UUID id) throws UserNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    protected User getUserEntity(UserDTO user) throws UserNotFoundException {
        return getUserEntity(user.getId());
    }

    protected UserDTO toDTO(User user) {
        return new UserDTO.UserDTOBuilder()
                .phone(user.getPhone())
                .acceptanceYear(Year.of(user.getAcceptanceYear()))
                .accountLocked(user.isAccountLocked())
                .activated(user.isActivated())
                .avatarUrl(user.getAvatarUrl())
                .cid(new Cid(user.getCid()))
                .email(new Email(user.getEmail()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gdpr(user.isGdpr())
                .id(user.getId())
                .nick(user.getNick())
                .userAgreement(user.isUserAgreement())
                .language(user.getLanguage())
                .build();
    }
}
