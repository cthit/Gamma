package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.image.domain.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class GroupsImageController {

    private final ImageFacade imageFacade;

    public GroupsImageController(ImageFacade imageFacade) {
        this.imageFacade = imageFacade;
    }

    @PutMapping("/groups/avatar/{id}")
    public ModelAndView editGroupAvatar(@RequestParam MultipartFile file, @PathVariable("id") UUID id) {
        try {
            this.imageFacade.setGroupAvatar(id, new ImageFile(file));
        } catch (ImageService.ImageCouldNotBeSavedException e) {
            throw new RuntimeException(e);
        }

        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/group-details :: group-avatar");
        mv.addObject("groupId", id);
        mv.addObject("random", Math.random());

        return mv;
    }

    @PutMapping("/groups/banner/{id}")
    public ModelAndView editGroupBanner(@RequestParam MultipartFile file, @PathVariable("id") UUID id) {
        try {
            this.imageFacade.setGroupBanner(id, new ImageFile(file));
        } catch (ImageService.ImageCouldNotBeSavedException e) {
            throw new RuntimeException(e);
        }

        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/group-details :: group-banner");
        mv.addObject("groupId", id);
        mv.addObject("random", Math.random());

        return mv;
    }

    @DeleteMapping("/groups/avatar/{id}")
    public ModelAndView deleteGroupAvatar(@PathVariable("id") UUID id) {
        this.imageFacade.removeGroupAvatar(id);

        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/group-details :: group-avatar");
        mv.addObject("groupId", id);
        mv.addObject("random", Math.random());

        return mv;
    }


    @DeleteMapping("/groups/banner/{id}")
    public ModelAndView deleteGroupBanner(@PathVariable("id") UUID id) {
        this.imageFacade.removeGroupBanner(id);
        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/group-details :: group-banner");
        mv.addObject("groupId", id);
        mv.addObject("random", Math.random());

        return mv;
    }
}