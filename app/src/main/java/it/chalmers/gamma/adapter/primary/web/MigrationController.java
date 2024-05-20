package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.migration.GammaMigration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MigrationController {

  private final GammaMigration gammaMigration;

  public MigrationController(GammaMigration gammaMigration) {
    this.gammaMigration = gammaMigration;
  }

  @GetMapping("/migration")
  public ModelAndView getMigration(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/migration");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/migration");
    }

    return mv;
  }

  public record MigrateForm(String apiKey) {}

  @PostMapping("/migration")
  public ModelAndView migrate(MigrateForm form) {
    gammaMigration.migrate(form.apiKey);

    return new ModelAndView("partial/migration-started");
  }
}
