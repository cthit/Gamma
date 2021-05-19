package it.chalmers.gamma.internal.supergroup.type.controller;

import it.chalmers.gamma.internal.supergroup.type.service.SuperGroupTypeFinder;
import it.chalmers.gamma.internal.supergroup.type.service.SuperGroupTypeName;
import it.chalmers.gamma.internal.supergroup.type.service.SuperGroupTypeService;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityHasUsagesException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.response.ErrorResponse;
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
@RequestMapping(value = "/supergroup")
public class SuperGroupTypeAdminController {

    private final SuperGroupTypeFinder superGroupTypeFinder;
    private final SuperGroupTypeService superGroupTypeService;

    public SuperGroupTypeAdminController(SuperGroupTypeFinder superGroupTypeFinder,
                                         SuperGroupTypeService superGroupTypeService) {
        this.superGroupTypeFinder = superGroupTypeFinder;
        this.superGroupTypeService = superGroupTypeService;
    }

    @GetMapping
    public List<SuperGroupTypeName> getSuperGroupTypes() {
        return this.superGroupTypeFinder.getAll();
    }

    private record AddSuperGroupType(SuperGroupTypeName name) { }


    @PostMapping
    public SuperGroupTypeAddedResponse addSuperGroupType(@Valid @RequestBody AddSuperGroupType request) {
        try {
            this.superGroupTypeService.create(request.name);
        } catch (EntityAlreadyExistsException e) {
            throw new SuperGroupTypeAlreadyExistsResponse();
        }
        return new SuperGroupTypeAddedResponse();
    }

    @DeleteMapping("/{name}")
    public SuperGroupTypeRemovedResponse removeSuperGroupType(@PathVariable("name") SuperGroupTypeName name) {
        try {
            this.superGroupTypeService.delete(name);
        } catch (EntityNotFoundException e) {
           throw new SuperGroupTypeDoesNotExistResponse();
        } catch (EntityHasUsagesException e) {
            throw new SuperGroupTypeIsUsedResponse();
        }
        return new SuperGroupTypeRemovedResponse();
    }

    private static class SuperGroupTypeAddedResponse extends SuccessResponse { }

    private static class SuperGroupTypeRemovedResponse extends SuccessResponse { }

    private static class SuperGroupTypeAlreadyExistsResponse extends ErrorResponse {
        private SuperGroupTypeAlreadyExistsResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class SuperGroupTypeDoesNotExistResponse extends ErrorResponse {
        private SuperGroupTypeDoesNotExistResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

    private static class SuperGroupTypeIsUsedResponse extends ErrorResponse {
        private SuperGroupTypeIsUsedResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
