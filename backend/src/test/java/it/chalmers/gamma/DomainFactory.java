package it.chalmers.gamma;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Helper functions
 */
public final class DomainFactory {

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
        return new GroupMember(
                p,
                new UnofficialPostName(""),
                u
        );
    }

    public static User u(String cid) {
        return u(cid, false, true);
    }

    public static User u(String cid, boolean locked, boolean gdprTrained) {
        return new User(
                UserId.generate(),
                0,
                new Cid(cid),
                new Email(cid + "@chalmers.it"),
                Language.SV,
                new Nick("N-" + cid),
                new Password("{noop}password"),
                new FirstName("F-" + cid),
                new LastName("L-" + cid),
                Instant.now(),
                new AcceptanceYear(2021),
                gdprTrained,
                locked,
                Optional.empty()
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
            EmailPrefix.empty()
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
    public static User  u2 = u("abcb", true, true);
    public static User u3 = u("abcc", false, false);
    public static User u4 = u("abcd");
    public static User u5 = u("abce", false, false);
    public static User u6 = u("abcf");
    public static User u7 = u("abcg", true, true);
    public static User u8 = u("abch");
    public static User u9 = u("abci");
    public static User u10 = u("abcj");
    public static User u11 = u("abck");

    public static Group digit18 = g("digit18", didit, List.of(gm(u1, chair), gm(u2, treasurer)));
    public static Group digit19 = g("digit19", digit, List.of(gm(u3, chair), gm(u4, member), gm(u2, member)));
    public static Group prit18 = g("prit18", sprit, List.of(gm(u1, chair), gm(u1, treasurer), gm(u2, member)));
    public static Group prit19 = g("prit19", prit, List.of(gm(u5, chair), gm(u6, treasurer), gm(u6, member)));
    public static Group drawit18 = g("drawit18", dragit, List.of(gm(u6, chair)));
    public static Group drawit19 = g("drawit19", drawit, List.of(gm(u1, chair), gm(u11, member)));
    public static Group styrit18 = g("styrit18", emeritus, List.of(gm(u7, chair), gm(u8, member), gm(u9, member)));
    public static Group styrit19 = g("styrit19", styrit, List.of(gm(u10, chair), gm(u11, treasurer)));

    public static void addAll(UserRepository userRepository, User... users) {
        for (User user : users) {
            userRepository.save(user);
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
