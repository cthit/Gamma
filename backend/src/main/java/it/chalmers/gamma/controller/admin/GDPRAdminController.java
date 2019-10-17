package it.chalmers.delta.controller.admin;

import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.ACCEPTANCE_YEAR;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.CID;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.FIRST_NAME;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.GDPR;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.ID;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.LAST_NAME;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.NICK;

import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.serializers.ITUserSerializer;
import it.chalmers.delta.requests.ChangeGDPRStatusRequest;
import it.chalmers.delta.response.GDPRStatusEdited;
import it.chalmers.delta.response.InputValidationFailedResponse;
import it.chalmers.delta.response.UserNotFoundResponse;
import it.chalmers.delta.service.ITUserService;
import it.chalmers.delta.util.InputValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/admin/gdpr")
public class GDPRAdminController {

    private final ITUserService itUserService;

    public GDPRAdminController(ITUserService itUserService) {
        this.itUserService = itUserService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editGDPRStatus(@PathVariable("id") String id,
                                                 @Valid @RequestBody ChangeGDPRStatusRequest request,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.itUserService.editGdpr(UUID.fromString(id), request.isGdpr());
        return new GDPRStatusEdited();
    }

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public List<JSONObject> getAllUserMini() {
        List<ITUser> itUsers = this.itUserService.loadAllUsers();
        List<ITUserSerializer.Properties> props =
                new ArrayList<>(Arrays.asList(
                        CID,
                        FIRST_NAME,
                        LAST_NAME,
                        NICK,
                        ACCEPTANCE_YEAR,
                        ID,
                        GDPR
                ));
        List<JSONObject> minifiedITUsers = new ArrayList<>();
        ITUserSerializer serializer = new ITUserSerializer(props);
        for (ITUser user : itUsers) {
            minifiedITUsers.add(serializer.serialize(user, null, null));
        }
        return minifiedITUsers;
    }
}
