package it.chalmers.gamma.internal.supergrouptype.controller;

import it.chalmers.gamma.domain.SuperGroup;
import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import it.chalmers.gamma.internal.supergrouptype.service.SuperGroupTypeService;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/internal/admin/supergrouptype")
public class SuperGroupTypeAdminController {

    private final SuperGroupTypeService superGroupTypeService;
    private final SuperGroupService superGroupService;

    public SuperGroupTypeAdminController(SuperGroupTypeService superGroupTypeService,
                                         SuperGroupService superGroupService) {
        this.superGroupTypeService = superGroupTypeService;
        this.superGroupService = superGroupService;
    }

    @GetMapping
    public List<SuperGroupType> getSuperGroupTypes() {
        return this.superGroupTypeService.getAll();
    }

    @GetMapping("/{type}/usage")
    public List<SuperGroup> getSuperGroupTypeUsage(@PathVariable("type") SuperGroupType type) {
        return this.superGroupService.getAllByType(type);
    }

    private record AddSuperGroupType(SuperGroupType type) { }
    
    @PostMapping
    public SuperGroupTypeAddedResponse addSuperGroupType(@RequestBody AddSuperGroupType request) {
        try {
            this.superGroupTypeService.create(request.type);
        } catch (SuperGroupTypeService.SuperGroupAlreadyExistsException e) {
            throw new SuperGroupTypeAlreadyExistsResponse();
        }
        return new SuperGroupTypeAddedResponse();
    }

    @DeleteMapping("/{name}")
    public SuperGroupTypeRemovedResponse removeSuperGroupType(@PathVariable("name") SuperGroupType name) {
        try {
            this.superGroupTypeService.delete(name);
        } catch (SuperGroupTypeService.SuperGroupNotFoundException e) {
           throw new SuperGroupTypeDoesNotExistResponse();
        } catch (SuperGroupTypeService.SuperGroupHasUsagesException e) {
            throw new SuperGroupTypeIsUsedResponse();
        }
        return new SuperGroupTypeRemovedResponse();
    }

    private static class SuperGroupTypeAddedResponse extends SuccessResponse { }

    private static class SuperGroupTypeRemovedResponse extends SuccessResponse { }

    private static class SuperGroupTypeAlreadyExistsResponse extends AlreadyExistsResponse { }

    private static class SuperGroupTypeDoesNotExistResponse extends NotFoundResponse { }

    private static class SuperGroupTypeIsUsedResponse extends ErrorResponse {
        private SuperGroupTypeIsUsedResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
