package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.settings.SettingsFacade;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/info-api-settings")
public class InfoApiSettingsAdminController {

    private final SettingsFacade settingsFacade;

    public InfoApiSettingsAdminController(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @GetMapping("/super-group-types")
    public List<String> getSuperGroupTypes() {
        return this.settingsFacade.getInfoApiSuperGroupTypes();
    }

    @PostMapping("/super-group-types")
    public InfoApiSuperGroupTypesSetResponse setSuperGroupTypes(@RequestBody SetSuperGroupTypesRequest setSuperGroupTypesRequest) {
        this.settingsFacade.setInfoSuperGroupTypes(setSuperGroupTypesRequest.superGroupTypes);
        return new InfoApiSuperGroupTypesSetResponse();
    }

    private record SetSuperGroupTypesRequest(List<String> superGroupTypes) {
    }

    private static class InfoApiSuperGroupTypesSetResponse extends SuccessResponse {
    }

}
