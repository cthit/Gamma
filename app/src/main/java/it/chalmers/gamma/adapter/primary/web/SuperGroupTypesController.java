package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SuperGroupTypesController {

  private final SuperGroupFacade superGroupFacade;

  public SuperGroupTypesController(SuperGroupFacade superGroupFacade) {
    this.superGroupFacade = superGroupFacade;
  }

  private ModelAndView createGetSuperGroupTypes(
      boolean htmxRequest, CreateType form, final BindingResult bindingResult) {
    List<String> types = this.superGroupFacade.getAllTypes();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/types");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/types");
    }

    if (form == null) {
      form = new CreateType("");
    }

    if (bindingResult != null) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    mv.addObject("form", form);
    mv.addObject(
        "types", types.stream().sorted(Comparator.comparing(String::toLowerCase)).toList());

    return mv;
  }

  @GetMapping("/types")
  public ModelAndView getSuperGroupTypes(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return this.createGetSuperGroupTypes(htmxRequest, null, null);
  }

  public ModelAndView createSuperGroupTypeNotFound(String type, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("type-details/not-found");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "type-details/not-found");
    }

    mv.addObject("type", type);

    return mv;
  }

  @GetMapping("/types/{type}")
  public ModelAndView getSuperGroupType(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("type") String type) {
    List<SuperGroupFacade.SuperGroupDTO> usages;
    try {
      usages = this.superGroupFacade.getAllSuperGroupsByType(type);
    } catch (SuperGroupRepository.TypeNotFoundRuntimeException e) {
      return createSuperGroupTypeNotFound(type, htmxRequest);
    }

    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("type-details/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "type-details/page");
    }

    mv.addObject("type", type);
    mv.addObject("usages", usages);

    return mv;
  }

  @PostMapping("/types")
  public ModelAndView createType(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      final CreateType form,
      final BindingResult bindingResult) {

    validateObject(form, bindingResult);

    try {
      if (!bindingResult.hasErrors()) {
        this.superGroupFacade.addType(form.type);
      }
    } catch (SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException e) {
      bindingResult.addError(
          new FieldError("form", "type", form.type, true, null, null, "Type already exist"));
    }

    if (bindingResult.hasErrors()) {
      return createGetSuperGroupTypes(htmxRequest, form, bindingResult);
    }

    return createGetSuperGroupTypes(htmxRequest, null, null);
  }

  public record CreateType(
      @ValidatedWith(SuperGroupType.SuperGroupTypeValidator.class) String type) {}

  @DeleteMapping("/types/{id}")
  public ModelAndView deleteType(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") String type)
      throws SuperGroupTypeRepository.SuperGroupTypeHasUsagesException,
          SuperGroupTypeRepository.SuperGroupTypeNotFoundException {
    this.superGroupFacade.removeType(type);

    return new ModelAndView("redirect:/types");
  }

  public record DeleteSuperGroupType() {}
}
