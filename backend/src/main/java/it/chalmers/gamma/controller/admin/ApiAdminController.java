package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.UserWebsiteService;
import it.chalmers.gamma.service.WebsiteView;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ApiAdminController {
    private final ITUserService itUserService;
    private final UserWebsiteService userWebsiteService;

    public ApiAdminController(ITUserService itUserService, UserWebsiteService userWebsiteService) {
        this.itUserService = itUserService;
        this.userWebsiteService = userWebsiteService;
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public JSONObject getMe(@RequestHeader("Authorization") String jwtTokenWithBearer, Principal principal) {
        ITUserSerializer serializer = new ITUserSerializer(ITUserSerializer.Properties.getAllProperties());
        List<ITUser> users = itUserService.loadAllUsers();
        return serializer.serialize(users.get(0), null);
    }
}
