package it.chalmers.gamma.app.supergroup;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.SuperGroupTypeRestrictions;
import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.supergroup.domain.*;
import it.chalmers.gamma.app.user.domain.Name;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupFacade extends Facade {

  private final SuperGroupRepository superGroupRepository;
  private final SuperGroupTypeRepository superGroupTypeRepository;
  private final GroupRepository groupRepository;
  private final SuperGroupTypeRestrictions superGroupTypeRestrictions;

  public SuperGroupFacade(
      AccessGuard accessGuard,
      SuperGroupRepository superGroupRepository,
      SuperGroupTypeRepository superGroupTypeRepository,
      GroupRepository groupRepository,
      SuperGroupTypeRestrictions superGroupTypeRestrictions) {
    super(accessGuard);
    this.superGroupRepository = superGroupRepository;
    this.superGroupTypeRepository = superGroupTypeRepository;
    this.groupRepository = groupRepository;
    this.superGroupTypeRestrictions = superGroupTypeRestrictions;
  }

  public void addType(String type)
      throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
    accessGuard.require(isAdmin());

    this.superGroupTypeRepository.add(new SuperGroupType(type));
  }

  public void removeType(String type)
      throws SuperGroupTypeRepository.SuperGroupTypeNotFoundException,
          SuperGroupTypeRepository.SuperGroupTypeHasUsagesException {
    accessGuard.require(isAdmin());

    this.superGroupTypeRepository.delete(new SuperGroupType(type));
  }

  public List<String> getAllTypes() {
    accessGuard.requireEither(isAdmin(), isSignedIn());

    return this.superGroupTypeRepository.getAll().stream().map(SuperGroupType::value).toList();
  }

  public List<SuperGroupTypeDTO> getAllTypesWithSuperGroups() {
    accessGuard.requireEither(isAdmin(), isApiWithScope(Scope.SUPER_GROUPS_READ));

    List<SuperGroupType> superGroupTypes =
        this.superGroupTypeRestrictions
            .configuredTypes()
            .orElseGet(() -> this.superGroupTypeRepository.getAll());

    return buildSuperGroupTree(superGroupTypes);
  }

  private List<SuperGroupTypeDTO> buildSuperGroupTree(List<SuperGroupType> superGroupTypes) {
    List<SuperGroupTypeDTO> output = new ArrayList<>();

    for (SuperGroupType type : superGroupTypes) {
      List<SuperGroupWithMembersDTO> superGroupsOutput = new ArrayList<>();
      for (SuperGroup superGroup : this.superGroupRepository.getAllByType(type)) {
        List<Group> groups = this.groupRepository.getAllBySuperGroup(superGroup.id());

        boolean hasAvatar =
            groups.stream()
                .map(Group::avatarUri)
                .map(Optional::isPresent)
                .reduce(false, (a, b) -> a || b);
        boolean hasBanner =
            groups.stream()
                .map(Group::bannerUri)
                .map(Optional::isPresent)
                .reduce(false, (a, b) -> a || b);
        List<GroupFacade.GroupMemberDTO> members =
            groups.stream()
                .flatMap(group -> group.groupMembers().stream())
                .map(GroupFacade.GroupMemberDTO::new)
                .sorted(Comparator.comparingInt(member -> member.post().order()))
                .toList();

        superGroupsOutput.add(
            new SuperGroupWithMembersDTO(
                new SuperGroupDTO(superGroup), hasBanner, hasAvatar, members));
      }

      output.add(new SuperGroupTypeDTO(type.value(), superGroupsOutput));
    }

    return output;
  }

  public record SuperGroupWithMembersDTO(
      SuperGroupDTO superGroup,
      boolean hasBanner,
      boolean hasAvatar,
      List<GroupFacade.GroupMemberDTO> members) {}

  public record SuperGroupTypeDTO(String type, List<SuperGroupWithMembersDTO> superGroups) {}

  public UUID createSuperGroup(NewSuperGroup newSuperGroup)
      throws SuperGroupRepository.SuperGroupAlreadyExistsException {
    accessGuard.require(isAdmin());

    SuperGroupId superGroupId = SuperGroupId.generate();

    this.superGroupRepository.save(
        new SuperGroup(
            superGroupId,
            0,
            new Name(newSuperGroup.name),
            new PrettyName(newSuperGroup.prettyName),
            new SuperGroupType(newSuperGroup.superGroupType),
            new Text(newSuperGroup.svDescription, newSuperGroup.enDescription)));

    return superGroupId.value();
  }

  public void updateSuperGroup(UpdateSuperGroup updateSuperGroup)
      throws SuperGroupRepository.SuperGroupNotFoundException {
    accessGuard.require(isAdmin());

    SuperGroup oldSuperGroup =
        this.superGroupRepository.get(new SuperGroupId(updateSuperGroup.id)).orElseThrow();
    SuperGroup newSuperGroup =
        SuperGroupBuilder.builder(oldSuperGroup)
            .name(new Name(updateSuperGroup.name))
            .prettyName(new PrettyName(updateSuperGroup.prettyName))
            .type(new SuperGroupType(updateSuperGroup.type))
            .description(new Text(updateSuperGroup.svDescription, updateSuperGroup.enDescription))
            .build();

    this.superGroupRepository.save(newSuperGroup);
  }

  public void deleteSuperGroup(UUID superGroupId)
      throws SuperGroupIsUsedException, SuperGroupNotFoundException {
    accessGuard.require(isAdmin());

    try {
      this.superGroupRepository.delete(new SuperGroupId(superGroupId));
    } catch (SuperGroupRepository.SuperGroupNotFoundException e) {
      throw new SuperGroupNotFoundException();
    } catch (SuperGroupRepository.SuperGroupIsUsedException e) {
      throw new SuperGroupIsUsedException();
    }
  }

  public List<SuperGroupDTO> getAll() {
    accessGuard.requireEither(isAdmin(), isClientApi(), isSignedIn());

    return this.superGroupRepository.getAll().stream().map(SuperGroupDTO::new).toList();
  }

  public List<SuperGroupDTO> getAllSuperGroupsByType(String superGroupType) {
    accessGuard.require(isAdmin());

    return this.superGroupRepository.getAllByType(new SuperGroupType(superGroupType)).stream()
        .map(SuperGroupDTO::new)
        .toList();
  }

  public Optional<SuperGroupDTO> get(UUID superGroupId) {
    accessGuard.require(isSignedIn());

    return this.superGroupRepository.get(new SuperGroupId(superGroupId)).map(SuperGroupDTO::new);
  }

  /** For v2 — no access guard, super group type restrictions enforced. */
  public List<SuperGroupDTO> fetchAllSuperGroups() {
    Optional<List<SuperGroupType>> allowed = this.superGroupTypeRestrictions.allowedTypes();
    return this.superGroupRepository.getAll().stream()
        .filter(superGroup -> allowsType(allowed, superGroup.type()))
        .map(SuperGroupDTO::new)
        .toList();
  }

  /** For v2 — no access guard, super group type restrictions enforced. */
  public Optional<SuperGroupDTO> fetchSuperGroup(UUID id) {
    Optional<List<SuperGroupType>> allowed = this.superGroupTypeRestrictions.allowedTypes();
    return this.superGroupRepository
        .get(new SuperGroupId(id))
        .filter(superGroup -> allowsType(allowed, superGroup.type()))
        .map(SuperGroupDTO::new);
  }

  /** For v2 — no access guard, super group type restrictions enforced. */
  public List<SuperGroupTypeDTO> fetchSuperGroupTree() {
    return buildSuperGroupTree(
        this.superGroupTypeRestrictions
            .allowedTypes()
            .orElseGet(() -> this.superGroupTypeRepository.getAll()));
  }

  private static boolean allowsType(Optional<List<SuperGroupType>> allowed, SuperGroupType type) {
    return allowed.map(types -> types.contains(type)).orElse(true);
  }

  public record NewSuperGroup(
      String name,
      String prettyName,
      String superGroupType,
      String svDescription,
      String enDescription) {}

  public record UpdateSuperGroup(
      UUID id,
      int version,
      String name,
      String prettyName,
      String type,
      String svDescription,
      String enDescription) {}

  public record SuperGroupDTO(
      UUID id,
      int version,
      String name,
      String prettyName,
      String type,
      String svDescription,
      String enDescription) {
    public SuperGroupDTO(SuperGroup superGroup) {
      this(
          superGroup.id().value(),
          superGroup.version(),
          superGroup.name().value(),
          superGroup.prettyName().value(),
          superGroup.type().value(),
          superGroup.description().sv().value(),
          superGroup.description().en().value());
    }
  }

  public static class SuperGroupNotFoundException extends Exception {}

  public static class SuperGroupIsUsedException extends Exception {}
}
