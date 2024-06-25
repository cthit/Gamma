package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;
import static it.chalmers.gamma.app.common.UUIDValidator.isValidUUID;

import it.chalmers.gamma.app.common.PrettyName.PrettyNameValidator;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.Name.NameValidator;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GroupsController {

  private final GroupFacade groupFacade;
  private final SuperGroupFacade superGroupFacade;
  private final UserFacade userFacade;
  private final PostFacade postFacade;

  public GroupsController(
      GroupFacade groupFacade,
      SuperGroupFacade superGroupFacade,
      UserFacade userFacade,
      PostFacade postFacade) {
    this.groupFacade = groupFacade;
    this.superGroupFacade = superGroupFacade;
    this.userFacade = userFacade;
    this.postFacade = postFacade;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

  @GetMapping("/groups")
  public ModelAndView getGroups(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<GroupFacade.GroupDTO> groups = this.groupFacade.getAll();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("groups/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "groups/page");
    }

    mv.addObject(
        "groups",
        groups.stream()
            .sorted(Comparator.comparing(group -> group.prettyName().toLowerCase()))
            .toList());

    return mv;
  }

  public record Member(String name, String post, UUID userId) {}

  public static class MyMembershipsForm {

    private MyMembershipsForm() {
      this.postNames = new HashMap<>();
    }

    private Map<String, String> postNames;

    public Map<String, String> getPostNames() {
      return postNames;
    }

    public void setPostNames(Map<String, String> postNames) {
      this.postNames = postNames;
    }
  }

  @GetMapping("/groups/{id}")
  public ModelAndView getGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") String groupId) {
    if (!isValidUUID(groupId)) {
      return createGroupNotFound(groupId, htmxRequest);
    }

    Optional<GroupFacade.GroupWithMembersDTO> group =
        this.groupFacade.getWithMembers(UUID.fromString(groupId));

    ModelAndView mv = new ModelAndView();
    if (group.isEmpty()) {
      return createGroupNotFound(groupId, htmxRequest);
    }

    if (htmxRequest) {
      mv.setViewName("group-details/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "group-details/page");
    }

    mv.addObject("group", group.get());
    mv.addObject(
        "members",
        group.get().groupMembers().stream()
            .map(
                groupMember -> {
                  String post = " - " + groupMember.post().enName();

                  if (groupMember.unofficialPostName() != null) {
                    post += " - " + groupMember.unofficialPostName();
                  }

                  return new Member(groupMember.user().nick(), post, groupMember.user().id());
                })
            .toList());
    mv.addObject("random", Math.random());

    boolean canEditImages = false;
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
      UUID myUserId = UUID.fromString(usernamePasswordAuthenticationToken.getName());

      canEditImages =
          group.get().groupMembers().stream()
              .anyMatch(groupMember -> groupMember.user().id().equals(myUserId));

      List<GroupFacade.GroupMemberDTO> myGroupMembers =
          group.get().groupMembers().stream()
              .filter(groupMember -> groupMember.user().id().equals(myUserId))
              .toList();
      mv.addObject("myMembers", myGroupMembers);

      MyMembershipsForm form = new MyMembershipsForm();

      myGroupMembers.forEach(
          groupMember -> {
            form.postNames.put(
                groupMember.post().id().toString(), groupMember.unofficialPostName());
          });

      mv.addObject("myMembershipsForm", form);
    }

    mv.addObject("canEditImages", canEditImages);

    return mv;
  }

  private ModelAndView createGroupNotFound(String groupId, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("group-details/not-found");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "group-details/not-found");
    }

    mv.addObject("id", groupId);

    return mv;
  }

  @GetMapping("/groups/{id}/cancel-edit")
  public ModelAndView getCancelEditGroup(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID id) {
    Optional<GroupFacade.GroupWithMembersDTO> group = this.groupFacade.getWithMembers(id);

    if (group.isEmpty()) {
      throw new RuntimeException();
    }

    ModelAndView mv = new ModelAndView();
    mv.setViewName("group-details/page :: group-details-article");

    mv.addObject("group", group.get());
    mv.addObject(
        "members",
        group.get().groupMembers().stream()
            .map(
                groupMember ->
                    new Member(
                        groupMember.user().nick(),
                        " - "
                            + groupMember.post().enName()
                            + " - "
                            + Objects.requireNonNullElse(groupMember.unofficialPostName(), ""),
                        groupMember.user().id()))
            .toList());

    return mv;
  }

  public static final class GroupForm {

    @ValidatedWith(NameValidator.class)
    private String name;

    @ValidatedWith(PrettyNameValidator.class)
    private String prettyName;

    private int version;
    private UUID superGroupId;
    private List<Member> members;

    public GroupForm() {
      this.members = new ArrayList<>();
    }

    public GroupForm(
        int version, String name, String prettyName, UUID superGroupId, List<Member> members) {
      this.version = version;
      this.name = name;
      this.prettyName = prettyName;
      this.superGroupId = superGroupId;
      this.members = members;
    }

    public int getVersion() {
      return version;
    }

    public void setVersion(int version) {
      this.version = version;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPrettyName() {
      return prettyName;
    }

    public void setPrettyName(String prettyName) {
      this.prettyName = prettyName;
    }

    public UUID getSuperGroupId() {
      return superGroupId;
    }

    public void setSuperGroupId(UUID superGroupId) {
      this.superGroupId = superGroupId;
    }

    public List<Member> getMembers() {
      return members;
    }

    public void setMembers(List<Member> members) {
      this.members = members;
    }

    public static final class Member {
      private UUID userId;
      private UUID postId;

      @ValidatedWith(UnofficialPostName.UnofficialPostNameValidator.class)
      private String unofficialPostName;

      public Member() {}

      public Member(UUID userId, UUID postId, String unofficialPostName) {
        this.userId = userId;
        this.postId = postId;
        this.unofficialPostName = unofficialPostName;
      }

      public UUID getUserId() {
        return userId;
      }

      public void setUserId(UUID userId) {
        this.userId = userId;
      }

      public UUID getPostId() {
        return postId;
      }

      public void setPostId(UUID postId) {
        this.postId = postId;
      }

      public String getUnofficialPostName() {
        return unofficialPostName;
      }

      public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
      }
    }
  }

  private ModelAndView createGetGroupEdit(UUID id, GroupForm form, BindingResult bindingResult) {
    Optional<GroupFacade.GroupWithMembersDTO> group = this.groupFacade.getWithMembers(id);

    if (group.isEmpty()) {
      throw new RuntimeException();
    }

    ModelAndView mv = new ModelAndView();
    mv.setViewName("group-details/edit-group");

    List<SuperGroupFacade.SuperGroupDTO> superGroups =
        this.superGroupFacade.getAll().stream()
            .sorted(Comparator.comparing(superGroup -> superGroup.prettyName().toLowerCase()))
            .toList();
    List<UserFacade.UserDTO> users = this.userFacade.getAll();
    List<PostFacade.PostDTO> posts = this.postFacade.getAll();

    if (form == null) {
      form =
          new GroupForm(
              group.get().version(),
              group.get().name(),
              group.get().prettyName(),
              group.get().superGroup().id(),
              group.get().groupMembers().stream()
                  .map(
                      groupMember ->
                          new GroupForm.Member(
                              groupMember.user().id(),
                              groupMember.post().id(),
                              groupMember.unofficialPostName()))
                  .toList());
    }

    mv.addObject("form", form);
    mv.addObject("superGroups", superGroups);
    mv.addObject("groupId", group.get().id());
    mv.addObject(
        "posts",
        posts.stream()
            .collect(Collectors.toMap(PostFacade.PostDTO::id, PostFacade.PostDTO::enName)));
    mv.addObject(
        "postKeys",
        posts.stream()
            .sorted(Comparator.comparing(post -> post.enName().toLowerCase()))
            .map(PostFacade.PostDTO::id)
            .toList());

    mv.addObject(
        "users",
        users.stream().collect(Collectors.toMap(UserFacade.UserDTO::id, UserFacade.UserDTO::nick)));
    mv.addObject(
        "userKeys",
        users.stream()
            .sorted(Comparator.comparing(user -> user.nick().toLowerCase()))
            .map(UserFacade.UserDTO::id)
            .toList());

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  private void checkForDuplicateEntries(GroupForm form, BindingResult bindingResult) {
    Set<Pair<UUID, UUID>> uniquePairs = new HashSet<>();
    for (int i = 0; i < form.getMembers().size(); i++) {
      GroupForm.Member member = form.getMembers().get(i);
      Pair<UUID, UUID> pair = Pair.of(member.getUserId(), member.getPostId());
      if (uniquePairs.contains(pair)) {
        bindingResult.addError(
            new FieldError("form", "members[" + i + "]", "Duplicate user/post entry"));
      } else {
        uniquePairs.add(pair);
      }
    }
  }

  @GetMapping("/groups/{id}/edit")
  public ModelAndView getGroupEdit(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID id) {
    return createGetGroupEdit(id, null, null);
  }

  @PutMapping("/groups/{id}")
  public ModelAndView updateGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") UUID id,
      HttpServletResponse response,
      final GroupForm form,
      final BindingResult bindingResult) {

    validateObject(form, bindingResult);

    checkForDuplicateEntries(form, bindingResult);

    if (!bindingResult.hasErrors() && this.groupFacade.groupWithNameAlreadyExists(id, form.name)) {
      bindingResult.addError(new FieldError("form", "name", "Group with name already exists"));
    }

    if (bindingResult.hasErrors()) {
      response.addHeader("HX-Reswap", "outerHTML");
      response.addHeader("HX-Retarget", "closest <article/>");
      return createGetGroupEdit(id, form, bindingResult);
    } else {
      this.groupFacade.update(
          new GroupFacade.UpdateGroup(
              id, form.version, form.name, form.prettyName, form.superGroupId));

      this.groupFacade.setMembers(
          id,
          form.members.stream()
              .map(m -> new GroupFacade.ShallowMember(m.userId, m.postId, m.unofficialPostName))
              .toList());

      return this.getGroup(htmxRequest, id.toString());
    }
  }

  @GetMapping("/groups/new-member")
  public ModelAndView getNewMember(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    List<UserFacade.UserDTO> users = this.userFacade.getAll();
    List<PostFacade.PostDTO> posts = this.postFacade.getAll();

    ModelAndView mv = new ModelAndView();
    mv.setViewName("group-details/add-member-to-group");

    mv.addObject(
        "posts",
        posts.stream()
            .collect(Collectors.toMap(PostFacade.PostDTO::id, PostFacade.PostDTO::enName)));
    mv.addObject(
        "postKeys",
        posts.stream()
            .sorted(Comparator.comparing(post -> post.enName().toLowerCase()))
            .map(PostFacade.PostDTO::id)
            .toList());

    mv.addObject(
        "users",
        users.stream().collect(Collectors.toMap(UserFacade.UserDTO::id, UserFacade.UserDTO::nick)));
    mv.addObject(
        "userKeys",
        users.stream()
            .sorted(Comparator.comparing(user -> user.nick().toLowerCase()))
            .map(UserFacade.UserDTO::id)
            .toList());

    return mv;
  }

  public ModelAndView createGetCreateGroup(
      boolean htmxRequest, GroupForm form, BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("create-group/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "create-group/page");
    }

    if (form == null) {
      form = new GroupForm();
    }

    List<UserFacade.UserDTO> users = this.userFacade.getAll();
    List<PostFacade.PostDTO> posts = this.postFacade.getAll();

    mv.addObject("form", form);
    mv.addObject("superGroups", this.superGroupFacade.getAll());
    mv.addObject(
        "users",
        users.stream().collect(Collectors.toMap(UserFacade.UserDTO::id, UserFacade.UserDTO::nick)));
    mv.addObject(
        "posts",
        posts.stream()
            .collect(Collectors.toMap(PostFacade.PostDTO::id, PostFacade.PostDTO::enName)));

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @GetMapping("/groups/create")
  public ModelAndView getCreateGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return createGetCreateGroup(htmxRequest, null, null);
  }

  @PostMapping("/groups/create")
  public ModelAndView createGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      final GroupForm form,
      final BindingResult bindingResult) {

    validateObject(form, bindingResult);

    checkForDuplicateEntries(form, bindingResult);

    if (bindingResult.hasErrors()) {
      return createGetCreateGroup(htmxRequest, form, bindingResult);
    }

    UUID groupId =
        this.groupFacade.create(
            new GroupFacade.NewGroup(form.name, form.prettyName, form.superGroupId));

    this.groupFacade.setMembers(
        groupId,
        form.members.stream()
            .map(m -> new GroupFacade.ShallowMember(m.userId, m.postId, m.unofficialPostName))
            .toList());

    return new ModelAndView("redirect:/groups/" + groupId);
  }

  @DeleteMapping("/groups/{groupId}")
  public ModelAndView deleteGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("groupId") UUID groupId) {
    this.groupFacade.delete(groupId);

    return new ModelAndView("redirect:/groups");
  }

  @PutMapping("/groups/{groupId}/my-posts")
  public ModelAndView updateUnofficialPostNames(
      @PathVariable("groupId") UUID groupId, MyMembershipsForm myMembershipsForm) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
      UUID myUserId = UUID.fromString(usernamePasswordAuthenticationToken.getName());

      myMembershipsForm.postNames.forEach(
          (postId, newUnofficialPostName) -> {
            groupFacade.changeUnofficialPostName(
                groupId, UUID.fromString(postId), myUserId, newUnofficialPostName);
          });
    }

    return new ModelAndView("redirect:/groups/" + groupId);
  }
}
