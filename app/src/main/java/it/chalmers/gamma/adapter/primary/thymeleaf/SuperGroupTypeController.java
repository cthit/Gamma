package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class SuperGroupTypeController {

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupTypeController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
    }

    @GetMapping("/types")
    public ModelAndView getSuperGroupTypes(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<String> types = this.superGroupFacade.getAllTypes();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/types");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/types");
        }

        var v = new CreateType("");

        mv.addObject("form", v);
        mv.addObject("types", types);

        return mv;
    }

    @PostMapping("/types")
    public ModelAndView createType(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
                                   final CreateType form,
                                   final BindingResult bindingResult) {
        try {
            this.superGroupFacade.addType(form.type);
        } catch (SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException e) {
            bindingResult.addError(
                    new FieldError(
                            "form",
                            "type",
                            "Type already exist"
                    )
            );
        }

        ModelAndView mv = new ModelAndView();
        if(bindingResult.hasErrors()) {
            mv.setViewName("pages/types :: create-type");
            mv.addObject("form", form);
            mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
        } else {
            mv.setViewName("partial/created-type");
            mv.addObject("type", form.type);
            mv.addObject("form", new CreateType(""));
        }

        return mv;
    }

    public record CreateType(String type) {}

    @DeleteMapping("/types/{id}")
    public ModelAndView deleteEditPost(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
                                       @PathVariable("id") String type,
                                       final DeleteSuperGroupType form,
                                       final BindingResult bindingResult,
                                       HttpServletResponse response) {
        try {
            this.superGroupFacade.removeType(type);
        } catch (SuperGroupTypeRepository.SuperGroupTypeNotFoundException e) {
            return new ModelAndView("common/empty");
        } catch (SuperGroupTypeRepository.SuperGroupTypeHasUsagesException e) {
            bindingResult.addError(
                    new ObjectError(
                            "global",
                            "Type has usages"
                    )
            );

            ModelAndView mv = new ModelAndView();

            mv.setViewName("partial/delete-type-error");
            mv.addObject("deleteForm", form);
            mv.addObject(BindingResult.MODEL_KEY_PREFIX + "deleteForm", bindingResult);

            response.addHeader("HX-Retarget", "#" + type + "-delete-form");
            response.addHeader("HX-Reswap", "outerHTML");

            return mv;
        }

        return new ModelAndView("common/empty");
    }

    public record DeleteSuperGroupType() { }

}
