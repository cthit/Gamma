package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class SuperGroupController {

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
    }

    @GetMapping("/super-groups")
    public ModelAndView getSuperGroups(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<SuperGroupFacade.SuperGroupDTO> superGroups = this.superGroupFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/super-groups");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/super-groups");
        }

        mv.addObject("superGroups", superGroups);

        return mv;
    }

    @GetMapping("/super-groups/{id}")
    public ModelAndView getSuperGroup(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @PathVariable("id") UUID id) {
        Optional<SuperGroupFacade.SuperGroupDTO> superGroup = this.superGroupFacade.get(id);

        if(superGroup.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/super-group-details");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/super-group-details");
        }

        mv.addObject("superGroup", superGroup.get());

        return mv;
    }

    public record UpdateSuperGroupForm(int version,
                                       String name,
                                       String prettyName,
                                       String type,
                                       String svDescription,
                                       String enDescription) {
    }

    @GetMapping("/super-groups/{id}/edit")
    public ModelAndView getSuperGroupEdit(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, @PathVariable("id") UUID id) {
        Optional<SuperGroupFacade.SuperGroupDTO> superGroup = this.superGroupFacade.get(id);

        if (superGroup.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("partial/edit-super-group");

        SuperGroupFacade.SuperGroupDTO sg = superGroup.get();

        mv.addObject("superGroupId", sg.id());
        mv.addObject("form", new UpdateSuperGroupForm(
                sg.version(),
                sg.name(),
                sg.prettyName(),
                sg.type(),
                sg.svDescription(),
                sg.enDescription()
        ));
        mv.addObject("types", this.superGroupFacade.getAllTypes());

        return mv;
    }

    @PutMapping("/super-groups/{id}")
    public ModelAndView updateSuperGroup(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
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
                            form.enDescription
                    )
            );
        } catch (SuperGroupRepository.SuperGroupNotFoundException e) {
            throw new RuntimeException(e);
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("pages/super-group-details");
        mv.addObject("superGroup", new SuperGroupFacade.SuperGroupDTO(
                id,
                form.version + 1,
                form.name,
                form.prettyName,
                form.type,
                form.svDescription,
                form.enDescription
        ));

        return mv;
    }

}