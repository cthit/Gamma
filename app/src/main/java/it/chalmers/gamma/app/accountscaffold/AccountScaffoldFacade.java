package it.chalmers.gamma.app.accountscaffold;

import static it.chalmers.gamma.app.authentication.AccessGuard.isApi;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeyAccountScaffoldSettings;
import it.chalmers.gamma.app.apikey.domain.settings.ApiKeySettingsRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.gdpr.GdprTrainedRepository;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class AccountScaffoldFacade extends Facade {

  private final GroupRepository groupRepository;
  private final GdprTrainedRepository gdprTrainedRepository;
  private final ApiKeySettingsRepository apiKeySettingsRepository;

  public AccountScaffoldFacade(
      AccessGuard accessGuard,
      GroupRepository groupRepository,
      GdprTrainedRepository gdprTrainedRepository,
      ApiKeySettingsRepository apiKeySettingsRepository) {
    super(accessGuard);
    this.groupRepository = groupRepository;
    this.gdprTrainedRepository = gdprTrainedRepository;
    this.apiKeySettingsRepository = apiKeySettingsRepository;
  }

  /**
   * Get all super groups that have the provided types and their "sub" groups with their members.
   */
  public List<AccountScaffoldSuperGroupDTO> getActiveSuperGroups() {
    this.accessGuard.require(isApi(ApiKeyType.ACCOUNT_SCAFFOLD));

    List<UserId> gdprTrained = this.gdprTrainedRepository.getAll();
    Map<SuperGroupId, SuperGroupWithGroups> superGroupMap = new HashMap<>();

    ApiAuthentication apiAuthentication =
        (ApiAuthentication) AuthenticationExtractor.getAuthentication();
    ApiKeyAccountScaffoldSettings settings =
        this.apiKeySettingsRepository.getAccountScaffoldSettings(apiAuthentication.get().id());

    this.groupRepository.getAll().stream()
        .filter(
            group ->
                settings.superGroupTypes().stream()
                    .anyMatch(row -> row.type().equals(group.superGroup().type())))
        .forEach(
            group -> {
              List<AccountScaffoldUserPostDTO> activeGroupMember =
                  group.groupMembers().stream()
                      .filter(groupMember -> gdprTrained.contains(groupMember.user().id()))
                      .map(AccountScaffoldUserPostDTO::new)
                      .toList();

              SuperGroupId superGroupId = group.superGroup().id();
              if (!superGroupMap.containsKey(superGroupId)) {
                superGroupMap.put(
                    superGroupId,
                    new SuperGroupWithGroups(
                        group.superGroup(), new ArrayList<>(List.of(new GroupWithMembers(group, new HashSet<>(activeGroupMember))))));
              } else {
                superGroupMap.get(superGroupId).groups.add(new GroupWithMembers(group, new HashSet<>(activeGroupMember)));
              }
            });

    return superGroupMap.values().stream()
        .map(
            superGroupWithGroups ->
                new AccountScaffoldSuperGroupDTO(
                    superGroupWithGroups.superGroup,
                    superGroupWithGroups.groups.stream().map(group -> new AccountScaffoldGroupDTO(group.group, new ArrayList<>(group.members))).toList(),
                    settings.superGroupTypes().stream()
                        .anyMatch(
                            row ->
                                row.type().equals(superGroupWithGroups.superGroup.type())
                                    && row.requiresManaged())))
        .toList();
  }

  /**
   * Returns the users that are active right now. Takes in a list of super group types to help
   * determine what kinds of groups that are deemed active. User must have
   * participated in gdpr training.
   */
  public List<AccountScaffoldUserDTO> getActiveUsers() {
    this.accessGuard.require(isApi(ApiKeyType.ACCOUNT_SCAFFOLD));

    List<UserId> gdprTrained = this.gdprTrainedRepository.getAll();

    ApiAuthentication apiAuthentication =
        (ApiAuthentication) AuthenticationExtractor.getAuthentication();
    ApiKeyAccountScaffoldSettings settings =
        this.apiKeySettingsRepository.getAccountScaffoldSettings(apiAuthentication.get().id());

    return this.groupRepository.getAll().stream()
        .filter(
            group ->
                settings.superGroupTypes().stream()
                    .anyMatch(
                        row ->
                            (row.type().equals(group.superGroup().type()))
                                && row.requiresManaged()))
        .flatMap(group -> group.groupMembers().stream())
        .map(GroupMember::user)
        .distinct()
        .filter(groupMember -> gdprTrained.contains(groupMember.id()))
        .map(AccountScaffoldUserDTO::new)
        .toList();
  }

  public record AccountScaffoldPostDTO(
      UUID postId, String svText, String enText, String emailPrefix) {
    public AccountScaffoldPostDTO(Post post) {
      this(
          post.id().value(),
          post.name().sv().value(),
          post.name().en().value(),
          post.emailPrefix().value());
    }
  }

  public record AccountScaffoldUserPostDTO(
      AccountScaffoldPostDTO post, AccountScaffoldUserDTO user) {
    public AccountScaffoldUserPostDTO(GroupMember groupMember) {
      this(
          new AccountScaffoldPostDTO(groupMember.post()),
          new AccountScaffoldUserDTO(groupMember.user()));
    }
  }

  public record AccountScaffoldUserDTO(
      String email, String cid, String firstName, String lastName, String nick) {
    public AccountScaffoldUserDTO(GammaUser user) {
      this(
          user.extended().email().value(),
          user.cid().value(),
          user.firstName().value(),
          user.lastName().value(),
          user.nick().value());
    }
  }

  public record AccountScaffoldGroupDTO(
          String name,
          String prettyName,
          List<AccountScaffoldUserPostDTO> members
  ) {
    public AccountScaffoldGroupDTO(
            Group group,
            List<AccountScaffoldUserPostDTO> members
    ) {
      this(
              group.name().value(),
              group.prettyName().value(),
              members
      );
    }
  }

  public record AccountScaffoldSuperGroupDTO(
      String name,
      String prettyName,
      String type,
      List<AccountScaffoldGroupDTO> groups,
      boolean useManagedAccount) {
    public AccountScaffoldSuperGroupDTO(
        SuperGroup superGroup,
        List<AccountScaffoldGroupDTO> groups,
        boolean useManagedAccount) {
      this(
          superGroup.name().value(),
          superGroup.prettyName().value(),
          superGroup.type().value(),
          groups,
          useManagedAccount);
    }
  }


  private static class GroupWithMembers {
    private final Group group;
    private final Set<AccountScaffoldUserPostDTO> members;

    private GroupWithMembers(Group group, Set<AccountScaffoldUserPostDTO> members) {
      this.group = group;
      this.members = members;
    }
  }

  private static class SuperGroupWithGroups {
    private final SuperGroup superGroup;
    private final List<GroupWithMembers> groups;

    private SuperGroupWithGroups(SuperGroup superGroup, List<GroupWithMembers> groups) {
      this.superGroup = superGroup;
      this.groups = groups;
    }
  }


}
