package it.chalmers.gamma.adapter.primary.api.v2;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/super-groups")
public class SuperGroupV2Controller {

  private final SuperGroupFacade superGroupFacade;
  private final GroupFacade groupFacade;

  public SuperGroupV2Controller(SuperGroupFacade superGroupFacade, GroupFacade groupFacade) {
    this.superGroupFacade = superGroupFacade;
    this.groupFacade = groupFacade;
  }

  @GetMapping
  public List<SuperGroupFacade.SuperGroupDTO> getSuperGroups() {
    return this.superGroupFacade.fetchAllSuperGroups();
  }

  @GetMapping("/{id}")
  public SuperGroupFacade.SuperGroupDTO getSuperGroup(@PathVariable("id") UUID id) {
    return this.superGroupFacade.fetchSuperGroup(id).orElseThrow(V2NotFoundResponse::new);
  }

  @GetMapping("/{id}/groups")
  public List<GroupFacade.GroupDTO> getSuperGroupGroups(@PathVariable("id") UUID id) {
    if (this.superGroupFacade.fetchSuperGroup(id).isEmpty()) {
      throw new V2NotFoundResponse();
    }
    return this.groupFacade.fetchGroupsBySuperGroup(id).stream()
        .map(gwm -> new GroupFacade.GroupDTO(gwm.id(), gwm.name(), gwm.prettyName(), gwm.superGroup(), gwm.version()))
        .toList();
  }

  @GetMapping("/{id}/members")
  public List<GroupFacade.GroupMemberDTO> getSuperGroupMembers(@PathVariable("id") UUID id) {
    if (this.superGroupFacade.fetchSuperGroup(id).isEmpty()) {
      throw new V2NotFoundResponse();
    }
    return this.groupFacade.fetchGroupsBySuperGroup(id).stream()
        .flatMap(g -> g.groupMembers().stream())
        .toList();
  }

  @GetMapping("/tree")
  public List<SuperGroupFacade.SuperGroupTypeDTO> getTree() {
    return this.superGroupFacade.fetchSuperGroupTree();
  }
}
