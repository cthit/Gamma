package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.GroupFacade;
import it.chalmers.gamma.domain.user.Name;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.group.GroupId;

import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/internal/admin/groups")
public final class GroupAdminController {

    private final GroupFacade groupFacade;

    public GroupAdminController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    private record CreateOrEditGroupRequest(Name name,
                                            PrettyName prettyName,
                                            SuperGroupId superGroup,
                                            Email email) { }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@RequestBody CreateOrEditGroupRequest request) {
//        try {
//            this.groupService.create(
//                    new GroupShallowDTO(
//                            GroupId.generate(),
//                            request.email,
//                            request.name,
//                            request.prettyName,
//                            request.superGroup
//                    )
//            );
//        } catch (GroupService.GroupAlreadyExistsException e) {
//            throw new GroupAlreadyExistsResponse();
//        }

        return new GroupCreatedResponse();
    }

    @PutMapping("/{id}")
    public GroupUpdatedResponse editGroup(@RequestBody CreateOrEditGroupRequest request,
                                          @PathVariable("id") GroupId id) {
//        try {
//            GroupShallowDTO group = new GroupShallowDTO(
//                    id,
//                    request.email,
//                    request.name,
//                    request.prettyName,
//                    request.superGroup
//            );
//            this.groupService.update(group);
//        } catch (GroupService.GroupNotFoundException e) {
//            throw new GroupNotFoundResponse();
//        }
//
        return new GroupUpdatedResponse();
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable("id") GroupId id) {
//        try {
//            this.groupService.delete(id);
//        } catch (GroupService.GroupNotFoundException e) {
//            throw new GroupNotFoundResponse();
//        }

        return new GroupDeletedResponse();
    }

    /**
     * This is the only thing a non-admin user that's part of the group can change
     */
    @PutMapping("/{id}/avatar")
    public GroupUpdatedResponse editAvatar(@PathVariable("id") GroupId id, @RequestParam MultipartFile file) {
/*
        try {
            String url = ImageUtils.saveImage(file, file.getName());
            GroupDTO group = this.groupFinder.get(id);
            this.groupService.update(
                    new GroupShallowDTO.GroupDTOBuilder()
                            .from(group)
                            .avatarUrl(url)
                            .build()
            );
        } catch (FileNotFoundResponse e) {
            throw new FileNotSavedException();
        } catch (EntityNotFoundException e) {
            throw new GroupNotFoundResponse();
        }
*/
        return new GroupUpdatedResponse();
    }

    private static class GroupCreatedResponse extends SuccessResponse { }

    private static class GroupDeletedResponse extends SuccessResponse { }

    private static class GroupUpdatedResponse extends SuccessResponse { }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

    private static class GroupAlreadyExistsResponse extends AlreadyExistsResponse { }

}
