package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.user.MeFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class MeController {

    private final MeFacade meFacade;
    private final ImageFacade imageFacade;

    public MeController(MeFacade meFacade,
                        ImageFacade imageFacade) {
        this.meFacade = meFacade;
        this.imageFacade = imageFacade;
    }

    @GetMapping("/me")
    public ModelAndView getMe(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();

        if(htmxRequest) {
            mv.setViewName("pages/me");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/me");
        }

        MeFacade.MeDTO me = this.meFacade.getMe();

        mv.addObject("me", me);
        mv.addObject("random", Math.random());

        return mv;
    }

    public record EditMe(String nick,
                         String firstName,
                         String lastName,
                         String email,
                         String language) {
    }

    @GetMapping("/me/edit")
    public ModelAndView getEditMe(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
        MeFacade.MeDTO me = this.meFacade.getMe();

        ModelAndView mv = new ModelAndView();

        EditMe form = new EditMe(
                me.nick(),
                me.firstName(),
                me.lastName(),
                me.email(),
                me.language()
        );

        mv.setViewName("partial/edit-me");
        mv.addObject("form", form);

        return mv;
    }

    @PutMapping("/me")
    public ModelAndView editMe(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
                               EditMe form) {
        this.meFacade.updateMe(new MeFacade.UpdateMe(
                form.nick,
                form.firstName,
                form.lastName,
                form.email,
                form.language
        ));

        ModelAndView mv = new ModelAndView();

        //TODO: Use only getMe
        MeFacade.MeDTO me = this.meFacade.getMe();

        mv.setViewName("pages/me :: userinfo");
        mv.addObject("me", new MeFacade.MeDTO(
                form.nick,
                form.firstName,
                form.lastName,
                me.cid(),
                form.email,
                me.id(),
                me.acceptanceYear(),
                me.groups(),
                form.language,
                me.isAdmin()
        ));

        return mv;
    }

    public record EditPasswordForm(String currentPassword, String newPassword, String confirmNewPassword) {}

    @GetMapping("/me/edit-password")
    public ModelAndView getEditPassword(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("partial/edit-me-password");
        mv.addObject("form", new EditPasswordForm("", "", ""));

        return mv;
    }

    @PutMapping("/me/edit-password")
    public ModelAndView editPassword(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
                                     EditPasswordForm form) {
        //TODO: Move validation to facade
        if (!form.newPassword.equals(form.confirmNewPassword)) {

        }

        this.meFacade.updatePassword(new MeFacade.UpdatePassword(
                form.currentPassword,
                form.newPassword
        ));

        MeFacade.MeDTO me = this.meFacade.getMe();

        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/me :: userinfo");
        mv.addObject("me", me);

        return mv;
    }

    @PutMapping("/me/avatar")
    public ModelAndView editAvatar(@RequestParam MultipartFile file) {
        try {
            this.meFacade.setAvatar(new ImageFile(file));
        } catch (ImageService.ImageCouldNotBeSavedException e) {
            throw new RuntimeException(e);
        }

        MeFacade.MeDTO me = this.meFacade.getMe();

        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/me :: me-avatar");
        mv.addObject("random", Math.random());
        mv.addObject("meId", me.id());

        return mv;
    }


}
