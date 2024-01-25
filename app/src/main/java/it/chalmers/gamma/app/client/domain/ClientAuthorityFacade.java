package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.client.domain.authority.ClientAuthorityRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isLocalRunner;

@Service
public class ClientAuthorityFacade extends Facade {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthorityFacade.class);

    private final ClientAuthorityRepository clientAuthorityRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SuperGroupRepository superGroupRepository;

    public ClientAuthorityFacade(AccessGuard accessGuard,
                                 ClientAuthorityRepository clientAuthorityRepository,
                                 UserRepository userRepository,
                                 PostRepository postRepository,
                                 SuperGroupRepository superGroupRepository) {
        super(accessGuard);
        this.clientAuthorityRepository = clientAuthorityRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.superGroupRepository = superGroupRepository;
    }

    public void create(UUID clientUid, String name) throws ClientAuthorityRepository.ClientAuthorityAlreadyExistsException {
        this.accessGuard.require(isAdmin());

        this.clientAuthorityRepository.create(new ClientUid(clientUid), new AuthorityName(name));
    }

    public void delete(UUID clientUid, String name) throws ClientAuthorityNotFoundException {
        this.accessGuard.require(isAdmin());

        try {
            this.clientAuthorityRepository.delete(new ClientUid(clientUid), new AuthorityName(name));
        } catch (ClientAuthorityRepository.ClientAuthorityNotFoundException e) {
            throw new ClientAuthorityNotFoundException();
        }
    }

    public Optional<ClientAuthorityDTO> get(UUID clientUid, String name) {
        this.accessGuard.require(isAdmin());

        return this.clientAuthorityRepository.get(new ClientUid(clientUid), new AuthorityName(name))
                .map(ClientAuthorityDTO::new);
    }

    @Transactional
    public void addSuperGroupToClientAuthority(UUID clientUid, String name, UUID superGroupId)
            throws ClientAuthorityNotFoundException, SuperGroupNotFoundException {
        this.accessGuard.require(isAdmin());

        Authority authority = this.clientAuthorityRepository.get(new ClientUid(clientUid), new AuthorityName(name))
                .orElseThrow(ClientAuthorityNotFoundException::new);

        List<SuperGroup> superGroups = new ArrayList<>(authority.superGroups());
        superGroups.add(this.superGroupRepository.get(new SuperGroupId(superGroupId))
                .orElseThrow(SuperGroupNotFoundException::new));

        this.clientAuthorityRepository.save(authority.withSuperGroups(superGroups));
    }

    @Transactional
    public void addUserToClientAuthority(UUID clientUid, String name, UUID userId) throws ClientAuthorityRepository.ClientAuthorityNotFoundRuntimeException, ClientAuthorityNotFoundException, UserNotFoundException {
        this.accessGuard.requireEither(
                isAdmin(),
                isLocalRunner()
        );

        Authority authority = this.clientAuthorityRepository.get(new ClientUid(clientUid), new AuthorityName(name))
                .orElseThrow(ClientAuthorityNotFoundException::new);

        List<GammaUser> newUsersList = new ArrayList<>(authority.users());
        GammaUser newUser = this.userRepository.get(new UserId(userId))
                .orElseThrow(UserNotFoundException::new);
        newUsersList.add(newUser);

        this.clientAuthorityRepository.save(authority.withUsers(newUsersList));
    }

    @Transactional
    public void removeSuperGroupFromClientAuthority(UUID clientUid, String name, UUID superGroupId)
            throws ClientAuthorityNotFoundException {
        this.accessGuard.require(isAdmin());

        Authority authority = this.clientAuthorityRepository.get(new ClientUid(clientUid), new AuthorityName(name))
                .orElseThrow(ClientAuthorityNotFoundException::new);

        List<SuperGroup> newSuperGroups = new ArrayList<>(authority.superGroups());
        for (int i = 0; i < newSuperGroups.size(); i++) {
            if (newSuperGroups.get(i).id().value().equals(superGroupId)) {
                newSuperGroups.remove(i);
                break;
            }
        }

        this.clientAuthorityRepository.save(authority.withSuperGroups(newSuperGroups));
    }

    public List<ClientAuthorityDTO> getAll(UUID clientUid) {
        this.accessGuard.require(isAdmin());

        var authorities = this.clientAuthorityRepository.getAllByClient(new ClientUid(clientUid));

        return authorities.stream().map(ClientAuthorityDTO::new).toList();
    }

    @Transactional
    public void removeUserFromClientAuthority(UUID clientUid, String name, UUID userId) throws ClientAuthorityNotFoundException {
        this.accessGuard.require(isAdmin());

        Authority authority = this.clientAuthorityRepository.get(new ClientUid(clientUid), new AuthorityName(name))
                .orElseThrow(ClientAuthorityNotFoundException::new);

        List<GammaUser> newUsers = new ArrayList<>(authority.users());
        for (int i = 0; i < newUsers.size(); i++) {
            if (newUsers.get(i).id().value().equals(userId)) {
                newUsers.remove(i);
                break;
            }
        }

        this.clientAuthorityRepository.save(authority.withUsers(newUsers));
    }

    public record ClientAuthorityDTO(
            UUID clientUid,
            String authorityName,
            List<SuperGroupFacade.SuperGroupDTO> superGroups,
            List<UserFacade.UserDTO> users) {

        public ClientAuthorityDTO(Authority authority) {
            this(authority.client().clientUid().value(),
                    authority.name().value(),
                    authority.superGroups().stream().map(SuperGroupFacade.SuperGroupDTO::new).toList(),
                    authority.users().stream().map(UserFacade.UserDTO::new).toList());
        }
    }

    public static class ClientAuthorityNotFoundException extends Exception {
    }

    public static class SuperGroupNotFoundException extends Exception {
    }

    public static class UserNotFoundException extends Exception {
    }

}
