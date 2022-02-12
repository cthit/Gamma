package it.chalmers.gamma;

import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Helper functions
 */
public final class DomainUtils {

    public static final Settings defaultSettings = new Settings(
            Instant.now().minus(1, ChronoUnit.DAYS),
            Collections.emptyList()
    );

    public static SuperGroup sg(String name, SuperGroupType type) {
        return new SuperGroup(
                SuperGroupId.generate(),
                0,
                new Name(name),
                new PrettyName(name.toUpperCase()),
                type,
                new Text()
        );
    }

    public static Group g(String name, SuperGroup sg, List<GroupMember> members) {
        return new Group(
                GroupId.generate(),
                0,
                new Name(name),
                new PrettyName(name.toUpperCase()),
                sg,
                members,
                Optional.empty(),
                Optional.empty()
        );
    }

    public static GroupMember gm(User u, Post p) {
        return gm(u, p, UnofficialPostName.none());
    }

    public static GroupMember gm(User u, Post p, UnofficialPostName unofficialPostName) {
        return new GroupMember(
                p,
                unofficialPostName,
                u
        );
    }

    public static User u(String cid) {
        return u(cid, false, true);
    }

    public static User u(String cid, boolean locked, boolean gdprTrained) {
        return new User(
                UserId.generate(),
                new Cid(cid),
                new Nick("N-" + cid),
                new FirstName("F-" + cid),
                new LastName("L-" + cid),
                new AcceptanceYear(2021),
                Language.SV,
                new UserExtended(
                        new Email(cid + "@chalmers.it"),
                        0,
                        true,
                        gdprTrained,
                        locked,
                        null
                )
        );
    }

    public static final SuperGroupType committee = new SuperGroupType("committee");
    public static final SuperGroupType board = new SuperGroupType("board");
    public static final SuperGroupType alumni = new SuperGroupType("alumni");
    public static final SuperGroupType society = new SuperGroupType("society");

    public static final Post chair = new Post(
            PostId.generate(),
            0,
            new Text(
                    "Ordförande",
                    "Chairman"
            ),
            new EmailPrefix("ordf")
    );

    public static final Post treasurer = new Post(
            PostId.generate(),
            0,
            new Text(
                    "Kassör",
                    "Treasurer"
            ),
            new EmailPrefix("kassor")
    );

    public static final Post member = new Post(
            PostId.generate(),
            0,
            new Text(
                    "Ledamot",
                    "Member"
            ),
            EmailPrefix.none()
    );

    public static SuperGroup digit = sg("digit", committee);
    public static SuperGroup didit = sg("didit", alumni);
    public static SuperGroup prit = sg("prit", committee);
    public static SuperGroup sprit = sg("sprit", alumni);
    public static SuperGroup drawit = sg("drawit", society);
    public static SuperGroup dragit = sg("dragit", alumni);
    public static SuperGroup styrit = sg("styrit", board);
    public static SuperGroup emeritus = sg("emeritus", alumni);

    public static User u0 = u("abcaa");
    public static User u1 = u("abca");
    public static User u2 = u("abcb", true, true);
    public static User u3 = u("abcc", false, false);
    public static User u4 = u("abcd");
    public static User u5 = u("abce", false, false);
    public static User u6 = u("abcf");
    public static User u7 = u("abcg", true, true);
    public static User u8 = u("abch");
    public static User u9 = u("abci");
    public static User u10 = u("abcj");
    public static User u11 = u("abck");

    public static Group digit17 = g("digit17", didit, List.of(gm(u11, chair), gm(u2, treasurer), gm(u4, member)));
    public static Group digit18 = g("digit18", didit, List.of(gm(u1, chair, new UnofficialPostName("root")), gm(u2, treasurer)));
    public static Group digit19 = g("digit19", digit, List.of(gm(u3, chair), gm(u4, member), gm(u2, member)));
    public static Group prit18 = g("prit18", sprit, List.of(gm(u1, chair, new UnofficialPostName("ChefChef")), gm(u1, treasurer), gm(u2, member)));
    public static Group prit19 = g("prit19", prit, List.of(gm(u5, chair), gm(u6, treasurer, new UnofficialPostName("Kas$$Chef")), gm(u6, member)));
    public static Group drawit18 = g("drawit18", dragit, List.of(gm(u6, chair)));
    public static Group drawit19 = g("drawit19", drawit, List.of(gm(u1, chair), gm(u11, member)));
    public static Group styrit18 = g("styrit18", emeritus, List.of(gm(u7, chair), gm(u8, member), gm(u9, member)));
    public static Group styrit19 = g("styrit19", styrit, List.of(gm(u10, chair), gm(u11, treasurer)));

    /**
     * Basically adds 1 to each version since they all have been saved.
     * Also removes extended.
     */
    public static Group asSaved(Group group) {
        return new Group(
                group.id(),
                group.version() + 1,
                group.name(),
                group.prettyName(),
                group.superGroup().withVersion(1),
                group.groupMembers()
                        .stream()
                        .map(groupMember -> new GroupMember(
                                groupMember.post().withVersion(1),
                                groupMember.unofficialPostName(),
                                removeUserExtended(groupMember.user())))
                        .toList(),
                group.avatarUri(),
                group.bannerUri()
        );
    }

    public static Group removeLockedUsers(Group group) {
        return group.withGroupMembers(
                group.groupMembers()
                        .stream()
                        .filter(groupMember -> !(groupMember.user().extended().locked() || !groupMember.user().extended().acceptedUserAgreement()))
                        .toList()
        );
    }

    public static Client removeUserExtended(Client client) {
        return client.withApprovedUsers(client.approvedUsers()
                .stream()
                .map(DomainUtils::removeUserExtended)
                .toList());
    }

    public static Group removeUserExtended(Group group) {
        return group.withGroupMembers(group.groupMembers()
                .stream()
                .map(groupMember -> groupMember.withUser(removeUserExtended(groupMember.user())))
                .toList());
    }

    public static User removeUserExtended(User user) {
        return user.withExtended(null);
    }

    public static User asSaved(User user) {
        return user.withExtended(user.extended().withVersion(1));
    }

    public static AuthorityLevel asSaved(AuthorityLevel authorityLevel) {
        return new AuthorityLevel(
                authorityLevel.name(),
                authorityLevel.posts()
                        .stream()
                        .map(superGroupPost -> new AuthorityLevel.SuperGroupPost(
                                superGroupPost.superGroup().withVersion(1),
                                superGroupPost.post().withVersion(1)
                        ))
                        .toList(),
                authorityLevel.superGroups()
                        .stream()
                        .map(superGroup -> superGroup.withVersion(1))
                        .toList(),
                authorityLevel.users()
                        .stream()
                        .map(DomainUtils::removeUserExtended)
                        .toList()
        );
    }

    public static void addAll(UserRepository userRepository, User... users) {
        addAll(userRepository, List.of(users));
    }

    public static void addAll(UserRepository userRepository, List<User> users) {
        for (User user : users) {
            userRepository.create(user, new UnencryptedPassword("password"));
        }
    }

    public static void addAll(SuperGroupRepository superGroupRepository, SuperGroup... superGroups) {
        for (SuperGroup superGroup : superGroups) {
            System.out.println(superGroup);
            superGroupRepository.save(superGroup);
        }
    }

    public static void addAll(GroupRepository groupRepository, Group... groups) throws GroupRepository.GroupNameAlreadyExistsException {
        for (Group group : groups) {
            groupRepository.save(group);
        }
    }

    public static void addAll(PostRepository postRepository, Post... posts) {
        addAll(postRepository, List.of(posts));
    }

    public static void addAll(PostRepository postRepository, List<Post> posts) {
        for (Post post : posts) {
            postRepository.save(post);
        }
    }

    public static void addAll(SuperGroupTypeRepository superGroupTypeRepository, SuperGroupType... types) throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        for (SuperGroupType type : types) {
            superGroupTypeRepository.add(type);
        }
    }

    public static void addGroup(SuperGroupTypeRepository superGroupTypeRepository,
                                SuperGroupRepository superGroupRepository,
                                UserRepository userRepository,
                                PostRepository postRepository,
                                GroupRepository groupRepository,
                                Group... groups)
            throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException, GroupRepository.GroupNameAlreadyExistsException {
        Set<SuperGroupType> types = new HashSet<>();
        Set<SuperGroup> superGroups = new HashSet<>();
        Set<Post> posts = new HashSet<>();
        Set<User> users = new HashSet<>();

        for (Group group : groups) {
            types.add(group.superGroup().type());
            superGroups.add(group.superGroup());
            group.groupMembers().forEach(groupMember -> {
                posts.add(groupMember.post());
                users.add(groupMember.user());
            });
        }

        for (SuperGroupType type : types) {
            superGroupTypeRepository.add(type);
        }
        for (SuperGroup superGroup : superGroups) {
            superGroupRepository.save(superGroup);
        }
        for (Post post : posts) {
            postRepository.save(post);
        }
        for (User user : users) {
            userRepository.create(user, new UnencryptedPassword("password"));
        }
        for (Group group : groups) {
            groupRepository.save(group);
        }
    }

    public static Post p() {
        PostId postId = PostId.generate();
        return new Post(
                PostId.generate(),
                0,
                new Text(
                        postId + " - swedish",
                        postId + " - english"
                ),
                new EmailPrefix("something")
        );
    }

}
