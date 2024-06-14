package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.common.PrettyName.PrettyNameValidator;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.Name.NameValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;
import static it.chalmers.gamma.app.common.UUIDValidator.isValidUUID;

@Controller
public class SuperGroupsController {

  private final SuperGroupFacade superGroupFacade;
  private final GroupFacade groupFacade;

  public SuperGroupsController(SuperGroupFacade superGroupFacade, GroupFacade groupFacade) {
    this.superGroupFacade = superGroupFacade;
    this.groupFacade = groupFacade;
  }

  @GetMapping("/super-groups")
  public ModelAndView getSuperGroups(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<SuperGroupFacade.SuperGroupDTO> superGroups = this.superGroupFacade.getAll();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/super-groups");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/super-groups");
    }

    mv.addObject(
        "superGroups",
        superGroups.stream()
            .sorted(Comparator.comparing(superGroup -> superGroup.prettyName().toLowerCase()))
            .toList());

    return mv;
  }

  @GetMapping("/super-groups/{id}")
  public ModelAndView getSuperGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") String superGroupId) {
    if (!isValidUUID(superGroupId)) {
      return createSuperGroupNotFound(superGroupId, htmxRequest);
    }

    Optional<SuperGroupFacade.SuperGroupDTO> superGroup =
        this.superGroupFacade.get(UUID.fromString(superGroupId));

    ModelAndView mv = new ModelAndView();
    if (superGroup.isEmpty()) {
      return createSuperGroupNotFound(superGroupId, htmxRequest);
    }

    if (htmxRequest) {
      mv.setViewName("pages/super-group-details");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/super-group-details");
    }

    mv.addObject("superGroup", superGroup.get());
    mv.addObject("usages", this.groupFacade.getAllBySuperGroup(superGroup.get().id()));

    return mv;
  }

  public ModelAndView createSuperGroupNotFound(String superGroupId, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/super-group-not-found");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/super-group-not-found");
    }

    mv.addObject("id", superGroupId);

    return mv;
  }

  public record UpdateSuperGroupForm(
      int version,
      String name,
      String prettyName,
      String type,
      String svDescription,
      String enDescription) {}

  @GetMapping("/super-groups/{id}/edit")
  public ModelAndView getSuperGroupEdit(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID id) {
    Optional<SuperGroupFacade.SuperGroupDTO> superGroup = this.superGroupFacade.get(id);

    if (superGroup.isEmpty()) {
      throw new RuntimeException();
    }

    ModelAndView mv = new ModelAndView();
    mv.setViewName("partial/edit-super-group");

    SuperGroupFacade.SuperGroupDTO sg = superGroup.get();

    mv.addObject("superGroupId", sg.id());
    mv.addObject(
        "form",
        new UpdateSuperGroupForm(
            sg.version(),
            sg.name(),
            sg.prettyName(),
            sg.type(),
            sg.svDescription(),
            sg.enDescription()));
    mv.addObject(
        "types",
        this.superGroupFacade.getAllTypes().stream()
            .sorted(Comparator.comparing(String::toLowerCase))
            .toList());

    return mv;
  }

  @GetMapping("/super-groups/{id}/cancel-edit")
  public ModelAndView getCancelSuperGroupEdit(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID id) {
    Optional<SuperGroupFacade.SuperGroupDTO> superGroup = this.superGroupFacade.get(id);

    if (superGroup.isEmpty()) {
      throw new RuntimeException();
    }

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/super-group-details :: super-group-info");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/super-group-details");
    }

    mv.addObject("superGroup", superGroup.get());

    return mv;
  }

  @PutMapping("/super-groups/{id}")
  public ModelAndView updateSuperGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") UUID id,
      final UpdateSuperGroupForm form,
      final BindingResult bindingResult) {
    try {
      this.superGroupFacade.updateSuperGroup(
          new SuperGroupFacade.UpdateSuperGroup(
              id,
              form.version,
              form.name,
              form.prettyName,
              form.type,
              form.svDescription,
              form.enDescription));
    } catch (SuperGroupRepository.SuperGroupNotFoundException e) {
      throw new RuntimeException(e);
    }

    ModelAndView mv = new ModelAndView();
    mv.setViewName("pages/super-group-details :: super-group-info");
    mv.addObject(
        "superGroup",
        new SuperGroupFacade.SuperGroupDTO(
            id,
            form.version + 1,
            form.name,
            form.prettyName,
            form.type,
            form.svDescription,
            form.enDescription));

    return mv;
  }

  @DeleteMapping("/super-groups/{id}")
  public ModelAndView deleteSuperGroup(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID superGroupId) {
    try {
      this.superGroupFacade.deleteSuperGroup(superGroupId);
    } catch (SuperGroupFacade.SuperGroupIsUsedException
        | SuperGroupFacade.SuperGroupNotFoundException e) {
      throw new RuntimeException(e);
    }

    return new ModelAndView("common/empty");
  }

  public record CreateSuperGroupForm(
      @ValidatedWith(NameValidator.class) String name,
      @ValidatedWith(PrettyNameValidator.class) String prettyName,
      String type,
      String svDescription,
      String enDescription) {}

  @GetMapping("/super-groups/create")
  public ModelAndView getCreateSuperGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      CreateSuperGroupForm form,
      BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("create-super-group/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "create-super-group/page");
    }

    if (form == null) {
      form = new CreateSuperGroupForm("", "", "", "", "");
    }

    mv.addObject("form", form);
    mv.addObject(
        "types",
        this.superGroupFacade.getAllTypes().stream()
            .sorted(Comparator.comparing(String::toLowerCase))
            .toList());

    if (bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @PostMapping("/super-groups")
  public ModelAndView createSuperGroup(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      CreateSuperGroupForm form,
      BindingResult bindingResult) {

    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      return getCreateSuperGroup(htmxRequest, form, bindingResult);
    }

    try {
      UUID superGroupId =
          this.superGroupFacade.createSuperGroup(
              new SuperGroupFacade.NewSuperGroup(
                  form.name, form.prettyName, form.type, form.svDescription, form.enDescription));

      return new ModelAndView("redirect:/super-groups/" + superGroupId);
    } catch (SuperGroupRepository.SuperGroupAlreadyExistsException e) {
      bindingResult.addError(new FieldError("form", "name", e.getMessage()));
      return getCreateSuperGroup(htmxRequest, form, bindingResult);
    }
  }
}
