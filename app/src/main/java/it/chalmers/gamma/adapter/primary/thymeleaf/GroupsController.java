package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class GroupsController {

    private final GroupFacade groupFacade;
    private final SuperGroupFacade superGroupFacade;
    private final UserFacade userFacade;
    private final PostFacade postFacade;

    public GroupsController(GroupFacade groupFacade,
                            SuperGroupFacade superGroupFacade,
                            UserFacade userFacade,
                            PostFacade postFacade) {
        this.groupFacade = groupFacade;
        this.superGroupFacade = superGroupFacade;
        this.userFacade = userFacade;
        this.postFacade = postFacade;
    }


    @GetMapping("/groups")
    public ModelAndView getGroups(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<GroupFacade.GroupDTO> groups = this.groupFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if (htmxRequest) {
            mv.setViewName("pages/groups");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/groups");
        }

        mv.addObject("groups", groups);

        return mv;
    }

    @GetMapping("/groups/{id}")
    public ModelAndView getGroup(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @PathVariable("id") UUID id) {
        Optional<GroupFacade.GroupWithMembersDTO> group = this.groupFacade.getWithMembers(id);

        if (group.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        if (htmxRequest) {
            mv.setViewName("pages/group-details");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/group-details");
        }

        mv.addObject("group", group.get());
        mv.addObject("members", group.get().groupMembers()
                .stream()
                .map(groupMember -> groupMember.user().nick() + " - " + groupMember.post().enName() + " - " + groupMember.unofficialPostName())
                .toList()
        );

        return mv;
    }

    //TODO: Investigate if this can be a record...
    public static final class UpdateGroupForm {
        private int version;
        private String name;
        private String prettyName;
        private UUID superGroupId;
        private List<Member> members;

        public UpdateGroupForm() {
            this.members = new ArrayList<>();
        }

        public UpdateGroupForm(int version,
                               String name,
                               String prettyName,
                               UUID superGroupId,
                               List<Member> members) {
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
            private String unofficialPostName;

            public Member() {

            }

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

    @GetMapping("/groups/{id}/edit")
    public ModelAndView getGroupEdit(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, @PathVariable("id") UUID id) {
        Optional<GroupFacade.GroupWithMembersDTO> group = this.groupFacade.getWithMembers(id);

        if (group.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("partial/edit-group");

        List<SuperGroupFacade.SuperGroupDTO> superGroups = this.superGroupFacade.getAll();
        List<UserFacade.UserDTO> users = this.userFacade.getAll();
        List<PostFacade.PostDTO> posts = this.postFacade.getAll();

        UpdateGroupForm form = new UpdateGroupForm(
                group.get().version(),
                group.get().name(),
                group.get().prettyName(),
                group.get().superGroup().id(),
                group.get().groupMembers()
                        .stream()
                        .map(groupMember -> new UpdateGroupForm.Member(
                                groupMember.user().id(),
                                groupMember.post().id(),
                                groupMember.unofficialPostName()
                        ))
                        .toList()
        );

        mv.addObject("form", form);
        mv.addObject("superGroups", superGroups);
        mv.addObject("groupId", group.get().id());
        mv.addObject("posts", posts
                .stream()
                .collect(Collectors.toMap(
                        PostFacade.PostDTO::id,
                        PostFacade.PostDTO::enName
                ))
        );

        mv.addObject("users", users
                .stream()
                .collect(Collectors.toMap(
                        UserFacade.UserDTO::id,
                        UserFacade.UserDTO::nick
                ))
        );

        return mv;
    }

    @PutMapping("/groups/{id}")
    public ModelAndView updateGroup(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
                                    @PathVariable("id") UUID id,
                                    final UpdateGroupForm form,
                                    final BindingResult bindingResult) {
        try {
            this.groupFacade.update(new GroupFacade.UpdateGroup(
                    id,
                    form.version,
                    form.name,
                    form.prettyName,
                    form.superGroupId
            ));
            this.groupFacade.setMembers(id, form.members
                    .stream()
                    .map(m -> new GroupFacade.ShallowMember(
                            m.userId,
                            m.postId,
                            m.unofficialPostName
                    ))
                    .toList()
            );
        } catch (GroupFacade.GroupAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        return this.getGroup(htmxRequest, id);
    }
}