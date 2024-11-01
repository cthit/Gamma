package it.chalmers.gamma.adapter.primary.api.info;

import it.chalmers.gamma.adapter.primary.api.NotFoundResponse;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Going to be used by chalmers.it to display groups and their members. A separate API since user
 * info are going to be returned, and that should be used with caution.
 */
@RestController
@RequestMapping(InfoV1ApiController.URI)
public class InfoV1ApiController {

  public static final String URI = "/api/info/v1";

  private final SuperGroupFacade superGroupFacade;
  private final UserFacade userFacade;

  public InfoV1ApiController(SuperGroupFacade superGroupFacade, UserFacade userFacade) {
    this.superGroupFacade = superGroupFacade;
    this.userFacade = userFacade;
  }

  @GetMapping("/users/{id}")
  public UserFacade.UserWithGroupsDTO getUser(@PathVariable("id") UUID id) {
    return this.userFacade.getWithGroups(id).orElseThrow(UserNotFoundResponse::new);
  }

  public record BlobItem(String type, List<BlobSuperGroupWithMembers> superGroups) {}

  public record BlobSuperGroupWithMembers(
      BlobSuperGroup superGroup,
      boolean hasBanner,
      boolean hasAvatar,
      List<BlobGroupMember> members) {

    public BlobSuperGroupWithMembers(SuperGroupFacade.SuperGroupWithMembersDTO s) {
      this(
          new BlobSuperGroup(s.superGroup()),
          s.hasBanner(),
          s.hasAvatar(),
          s.members().stream().map(BlobGroupMember::new).toList());
    }
  }

  public record BlobSuperGroup(
      UUID id,
      String name,
      String prettyName,
      String type,
      String svDescription,
      String enDescription) {

    public BlobSuperGroup(SuperGroupFacade.SuperGroupDTO superGroup) {
      this(
          superGroup.id(),
          superGroup.name(),
          superGroup.prettyName(),
          superGroup.type(),
          superGroup.svDescription(),
          superGroup.enDescription());
    }
  }

  public record BlobGroupMember(BlobUser user, BlobPost post, String unofficialPostName) {
    public BlobGroupMember(GroupFacade.GroupMemberDTO groupMember) {
      this(
          new BlobUser(groupMember.user()),
          new BlobPost(groupMember.post()),
          groupMember.unofficialPostName());
    }
  }

  public record BlobUser(
      String cid, String nick, String firstName, String lastName, UUID id, int acceptanceYear) {
    public BlobUser(UserFacade.UserDTO user) {
      this(
          user.cid(),
          user.nick(),
          user.firstName(),
          user.lastName(),
          user.id(),
          user.acceptanceYear());
    }
  }

  public record BlobPost(UUID id, String svName, String enName, String emailPrefix) {
    public BlobPost(PostFacade.PostDTO post) {
      this(post.id(), post.svName(), post.enName(), post.emailPrefix());
    }
  }

  @GetMapping("/blob")
  public List<BlobItem> getGroups() {
    return this.superGroupFacade.getAllTypesWithSuperGroups().stream()
        .map(
            item ->
                new BlobItem(
                    item.type(),
                    item.superGroups().stream().map(BlobSuperGroupWithMembers::new).toList()))
        .toList();
  }

  private static class UserNotFoundResponse extends NotFoundResponse {}
}
