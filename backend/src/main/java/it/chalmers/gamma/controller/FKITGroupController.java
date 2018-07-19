package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.response.GetGroupResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.service.FKITService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/groups")
public class FKITGroupController {

    private FKITService fkitService;
    public FKITGroupController(FKITService fkitService){
        this.fkitService = fkitService;
    }

    @RequestMapping(value = "/{group}", method = RequestMethod.GET)
    public ResponseEntity getGroupInfo(@PathVariable("group") String group){
        if(!fkitService.groupExists(group)){
            return new GroupDoesNotExistResponse();
        }
        return new GetGroupResponse(fkitService.getGroupInfo(group));
    }
}
