package it.chalmers.gamma.adapter.primary.api.goldapps;

import it.chalmers.gamma.app.goldapps.GoldappsFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Used by cthit/goldapps to sync Google accounts for the student division.
 * If you need changes, then create a new version of the API.
 */
@RestController
@RequestMapping(GoldappsV1ApiController.URI)
public class GoldappsV1ApiController {

    public static final String URI = "/api/goldapps/v1";

    private final GoldappsFacade goldappsFacade;

    public GoldappsV1ApiController(GoldappsFacade goldappsFacade) {
        this.goldappsFacade = goldappsFacade;
    }

    @GetMapping("/supergroups")
    public List<GoldappsFacade.GoldappsSuperGroupDTO> getSuperGroups(@RequestParam String types) {
        return this.goldappsFacade.getActiveSuperGroups(List.of(types.split(";")));
    }

    @GetMapping("/users")
    public List<GoldappsFacade.GoldappsUserDTO> getUsers(@RequestParam String types) {
        return this.goldappsFacade.getActiveUsers(List.of(types.split(";")));
    }

}
