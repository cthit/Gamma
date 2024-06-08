package it.chalmers.gamma.app.migration;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.*;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.domain.*;
import it.chalmers.gamma.app.user.domain.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GammaMigration {

  private final SuperGroupTypeRepository superGroupTypeRepository;
  private final SuperGroupRepository superGroupRepository;
  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;

  public GammaMigration(
      SuperGroupTypeRepository superGroupTypeRepository,
      SuperGroupRepository superGroupRepository,
      GroupRepository groupRepository,
      UserRepository userRepository,
      PostRepository postRepository) {
    this.superGroupTypeRepository = superGroupTypeRepository;
    this.superGroupRepository = superGroupRepository;
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.postRepository = postRepository;
  }

  public enum OldLanguage {
    sv,
    en
  }

  public record OldUser(
      UUID id,
      String cid,
      String nick,
      String firstName,
      String lastName,
      String email,
      String phone,
      OldLanguage language,
      String avatarUrl,
      boolean gdpr,
      boolean userAgreement,
      boolean accountLocked,
      int acceptanceYear,
      boolean activated) {}

  public record OldText(String sv, String en) {}

  public record OldGroupMember(
      OldPost post,
      UUID id,
      String cid,
      String nick,
      String firstName,
      String lastName,
      String email,
      String phone,
      OldLanguage language,
      String avatarUrl,
      boolean gdpr,
      boolean userAgreement,
      boolean accountLocked,
      int acceptanceYear,
      boolean activated,
      String unofficialPostName) {}

  public record OldGroup(
      UUID id,
      String name,
      String prettyName,
      OldText description,
      List<OldGroupMember> groupMembers,
      OldSuperGroup superGroup) {}

  public record GroupsResponse(List<OldGroup> groups) {}

  public enum OldSuperGroupType {
    ALUMNI,
    COMMITTEE,
    SOCIETY,
    FUNCTIONARIES,
    ADMIN
  }

  public record OldSuperGroup(
      UUID id, String name, String prettyName, String email, OldSuperGroupType type) {}

  public record OldPost(UUID id, String sv, String en, String emailPrefix) {}

  public record OldData(
      List<OldUser> users,
      List<OldGroup> groups,
      List<OldSuperGroup> superGroups,
      List<OldPost> posts) {}

  @Async
  public void migrate(String apiKey) {
    var gammaData = getDataFromGamma(apiKey);

    // We got everything we need, let's delete E V E R Y T H I N G
    deleteEverything();

    // After everything has been deleted, let's create E V E R Y T H I N G
    createEverything(gammaData);
  }

  private OldData getDataFromGamma(String apiKey) {
    var client = new GammaClient(apiKey);

    // Before starting to remove things, we need to gather all the information we need.
    var users = client.getList("/admin/users", new ParameterizedTypeReference<List<OldUser>>() {});

    var groupsResponse = client.get("/admin/groups", GroupsResponse.class);
    var groups = groupsResponse.groups;

    var superGroups =
        client.getList("/superGroups", new ParameterizedTypeReference<List<OldSuperGroup>>() {});

    var posts = client.getList("/groups/posts", new ParameterizedTypeReference<List<OldPost>>() {});

    return new OldData(users, groups, superGroups, posts);
  }

  private void deleteEverything() {
    for (GammaUser user : this.userRepository.getAll()) {
      if (user.cid().value().equals("admin")) {
        continue;
      }

      this.userRepository.delete(user.id());
    }

    for (Group group : this.groupRepository.getAll()) {
      try {
        this.groupRepository.delete(group.id());
      } catch (GroupRepository.GroupNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    for (SuperGroup superGroup : this.superGroupRepository.getAll()) {
      try {
        this.superGroupRepository.delete(superGroup.id());
      } catch (SuperGroupRepository.SuperGroupNotFoundException
          | SuperGroupRepository.SuperGroupIsUsedException e) {
        throw new RuntimeException(e);
      }
    }

    for (SuperGroupType superGroupType : this.superGroupTypeRepository.getAll()) {
      try {
        this.superGroupTypeRepository.delete(superGroupType);
      } catch (SuperGroupTypeRepository.SuperGroupTypeNotFoundException
          | SuperGroupTypeRepository.SuperGroupTypeHasUsagesException e) {
        throw new RuntimeException(e);
      }
    }

    for (Post post : this.postRepository.getAll()) {
      try {
        this.postRepository.delete(post.id());
      } catch (PostRepository.PostNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private SuperGroup convertSuperGroup(OldSuperGroup superGroup) {
    return new SuperGroup(
        new SuperGroupId(superGroup.id),
        0,
        new Name(superGroup.name.replaceAll(" ", "-")),
        new PrettyName(superGroup.prettyName.replaceAll("'", "")),
        new SuperGroupType(superGroup.type.name()),
        new Text("", ""));
  }

  private Post convertPost(OldPost post) {
    return new Post(
        new PostId(post.id), 0, new Text(post.sv, post.en), new EmailPrefix(post.emailPrefix));
  }

  private GammaUser convertUser(OldGroupMember user) {
    return new GammaUser(
        new UserId(user.id),
        new Cid(user.cid.replaceAll("\\d", "")),
        new Nick(user.nick),
        new FirstName(user.firstName),
        new LastName(user.lastName),
        new AcceptanceYear(user.acceptanceYear),
        Language.valueOf(user.language.toString().toUpperCase()),
        new UserExtended(new Email(user.email), 0, false, null));
  }

  private GammaUser convertUser(OldUser user) {
    return new GammaUser(
        new UserId(user.id),
        new Cid(user.cid.replaceAll("\\d", "")),
        new Nick(user.nick),
        new FirstName(user.firstName),
        new LastName(user.lastName),
        new AcceptanceYear(user.acceptanceYear),
        Language.valueOf(user.language.toString().toUpperCase()),
        new UserExtended(new Email(user.email), 0, false, null));
  }

  private void createEverything(OldData data) {
    for (OldSuperGroupType type : OldSuperGroupType.values()) {
      try {
        this.superGroupTypeRepository.add(new SuperGroupType(type.name()));
      } catch (SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException e) {
        throw new RuntimeException(e);
      }
    }

    for (OldSuperGroup superGroup : data.superGroups) {
      try {
        this.superGroupRepository.save(convertSuperGroup(superGroup));
      } catch (Exception e) {
        System.out.println("Error creating supergroup: " + e.getMessage());
      }
    }

    for (OldPost post : data.posts) {
      try {
        this.postRepository.save(convertPost(post));
      } catch (Exception e) {
        System.out.println("Error creating post: " + e.getMessage());
      }
    }

    for (OldUser user : data.users) {
      if (user.cid.equals("admin")) {
        continue;
      }

      try {
        this.userRepository.create(convertUser(user), null);
      } catch (Exception e) {
        System.out.println("Failed to create the user");
      }
    }

    for (OldGroup group : data.groups) {

      if (group.superGroup.type.equals(OldSuperGroupType.ADMIN)) {
        continue;
      }

      try {
        this.groupRepository.save(
            new Group(
                new GroupId(group.id),
                0,
                new Name(group.name.replaceAll(" ", "-")),
                new PrettyName(group.prettyName.replaceAll("'", " ").replaceAll(" {2}", " ")),
                convertSuperGroup(group.superGroup),
                group.groupMembers.stream()
                    .map(
                        groupMember ->
                            new GroupMember(
                                convertPost(groupMember.post),
                                new UnofficialPostName(groupMember.unofficialPostName),
                                convertUser(groupMember)))
                    .toList(),
                Optional.empty(),
                Optional.empty()));
      } catch (Exception e) {
        System.out.println("Failed to create the group");
      }
    }
  }
}
