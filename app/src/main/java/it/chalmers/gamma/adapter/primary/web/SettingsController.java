package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.settings.SettingsFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SettingsController {

  private final SettingsFacade settingsFacade;
  private final SuperGroupFacade superGroupFacade;

  public SettingsController(SettingsFacade settingsFacade, SuperGroupFacade superGroupFacade) {
    this.settingsFacade = settingsFacade;
    this.superGroupFacade = superGroupFacade;
  }

  public static final class Settings {
    public List<String> superGroupTypesForInfo;

    public List<String> getSuperGroupTypesForInfo() {
      return superGroupTypesForInfo;
    }

    public void setSuperGroupTypesForInfo(List<String> superGroupTypesForInfo) {
      this.superGroupTypesForInfo = superGroupTypesForInfo;
    }
  }

  @GetMapping("/settings")
  public ModelAndView getSettings(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/settings");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/settings");
    }

    Settings form = new Settings();
    form.superGroupTypesForInfo = this.settingsFacade.getInfoApiSuperGroupTypes();

    mv.addObject("form", form);

    return mv;
  }

  @GetMapping("/settings/new-super-group")
  public ModelAndView getNewSuperGroupType(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/new-super-group-type-to-info-api");
    mv.addObject("superGroupTypes", this.superGroupFacade.getAllTypes());

    return mv;
  }

  @PutMapping("/settings")
  public ModelAndView updateSettings(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, Settings form) {
    this.settingsFacade.setInfoSuperGroupTypes(form.superGroupTypesForInfo);

    return new ModelAndView("redirect:/settings");
  }
}
