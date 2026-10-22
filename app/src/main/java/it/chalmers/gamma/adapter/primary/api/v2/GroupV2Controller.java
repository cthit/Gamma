package it.chalmers.gamma.adapter.primary.api.v2;

import it.chalmers.gamma.app.group.GroupFacade;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/groups")
public class GroupV2Controller {

  private final GroupFacade groupFacade;

  public GroupV2Controller(GroupFacade groupFacade) {
    this.groupFacade = groupFacade;
  }

  @GetMapping
  public List<GroupFacade.GroupDTO> getGroups() {
    return this.groupFacade.fetchAllGroups();
  }

  @GetMapping("/{id}")
  public GroupFacade.GroupDTO getGroup(@PathVariable("id") UUID id) {
    return this.groupFacade.fetchGroup(id).orElseThrow(V2NotFoundResponse::new);
  }

  @GetMapping("/{id}/members")
  public GroupFacade.GroupWithMembersDTO getGroupMembers(@PathVariable("id") UUID id) {
    return this.groupFacade.fetchGroupWithMembers(id).orElseThrow(V2NotFoundResponse::new);
  }
}
