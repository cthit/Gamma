package it.chalmers.gamma.app.goldapps;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.GammaUser;
import org.springframework.stereotype.Service;

import java.util.*;

import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;

@Service
public class GoldappsFacade extends Facade {

    private final GroupRepository groupRepository;

    public GoldappsFacade(AccessGuard accessGuard,
                          GroupRepository groupRepository) {
        super(accessGuard);
        this.groupRepository = groupRepository;
    }

    public List<GoldappsSuperGroupDTO> getSuperGroupsByTypes(List<String> superGroupTypes) {
        this.accessGuard.require(isApi(ApiKeyType.GOLDAPPS));

        // grupper
        // grupper som är activa


        return null;
    }

    /**
     * Get all super groups that have the provided types
     * and members that are a part of groups that has each supergroup
     */
    public List<GoldappsSuperGroupDTO> getActiveSuperGroups(List<String> superGroupTypes) {
        this.accessGuard.require(isApi(ApiKeyType.GOLDAPPS));

        Map<SuperGroupId, SuperGroupWithMembers> superGroupMap = new HashMap<>();

        this.groupRepository.getAll()
                .stream()
                .filter(group -> superGroupTypes.contains(group.superGroup().type().value()))
                .forEach(group -> {
                    List<GoldappsUserPostDTO> activeGroupMember = group.groupMembers()
                            .stream()
                            .filter(groupMember -> !groupMember.user().extended().locked())
                            //TODO:
//                            .filter(groupMember -> groupMember.user().extended().gdprTrained())
                            .map(GoldappsUserPostDTO::new)
                            .toList();

                    SuperGroupId superGroupId = group.superGroup().id();
                    if (!superGroupMap.containsKey(superGroupId)) {
                        superGroupMap.put(
                                superGroupId,
                                new SuperGroupWithMembers(
                                        group.superGroup(),
                                        new HashSet<>(activeGroupMember)
                                ));
                    } else {
                        superGroupMap.get(superGroupId).members.addAll(activeGroupMember);
                    }
                });

        return superGroupMap
                .values()
                .stream()
                .map(superGroupWithMembers -> new GoldappsSuperGroupDTO(
                        superGroupWithMembers.superGroup,
                        new ArrayList<>(superGroupWithMembers.members)
                ))
                .toList();
    }

    /**
     * Returns the users that are active right now.
     * Takes in a list of super group types to help determine
     * what kinds of groups that are deemed active.
     * User must also be not locked, and have participated in gdpr training.
     */
    public List<GoldappsUserDTO> getActiveUsers(List<String> superGroupTypes) {
        this.accessGuard.require(isApi(ApiKeyType.GOLDAPPS));

        return this.groupRepository.getAll()
                .stream()
                .filter(group -> superGroupTypes.contains(group.superGroup().type().value()))
                .flatMap(group -> group.groupMembers().stream())
                .map(GroupMember::user)
                .distinct()
                .filter(user -> !user.extended().locked())
                //TODO:
//                .filter(user -> user.extended().gdprTrained())
                .map(GoldappsUserDTO::new)
                .toList();
    }

    public record GoldappsPostDTO(UUID postId,
                                  String svText,
                                  String enText,
                                  String emailPrefix) {
        public GoldappsPostDTO(Post post) {
            this(post.id().value(),
                    post.name().sv().value(),
                    post.name().en().value(),
                    post.emailPrefix().value());
        }
    }

    public record GoldappsUserPostDTO(GoldappsPostDTO post,
                                      GoldappsUserDTO user) {
        public GoldappsUserPostDTO(GroupMember groupMember) {
            this(new GoldappsPostDTO(groupMember.post()),
                    new GoldappsUserDTO(groupMember.user()));
        }
    }

    public record GoldappsUserDTO(String email,
                                  String cid,
                                  String firstName,
                                  String lastName,
                                  String nick) {
        public GoldappsUserDTO(GammaUser user) {
            this(user.extended().email().value(),
                    user.cid().value(),
                    user.firstName().value(),
                    user.lastName().value(),
                    user.nick().value());
        }
    }

    public record GoldappsSuperGroupDTO(String name,
                                        String prettyName,
                                        String type,
                                        List<GoldappsUserPostDTO> members) {
        public GoldappsSuperGroupDTO(SuperGroup superGroup, List<GoldappsUserPostDTO> members) {
            this(superGroup.name().value(),
                    superGroup.prettyName().value(),
                    superGroup.type().value(),
                    members
            );
        }

    }

    private static class SuperGroupWithMembers {
        private final SuperGroup superGroup;
        private final Set<GoldappsUserPostDTO> members;

        private SuperGroupWithMembers(SuperGroup superGroup, Set<GoldappsUserPostDTO> members) {
            this.superGroup = superGroup;
            this.members = members;
        }
    }


}
