package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.adapter.secondary.image.ImageFile.ImageFileValidator;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.image.domain.ImageService;
import it.chalmers.gamma.app.validation.FailedValidation;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GroupsImageController {

  private final GroupFacade groupFacade;
  private final ImageFacade imageFacade;

  public GroupsImageController(GroupFacade groupFacade, ImageFacade imageFacade) {
    this.groupFacade = groupFacade;
    this.imageFacade = imageFacade;
  }

  @PutMapping("/groups/avatar/{id}")
  public ModelAndView editGroupAvatar(
      @RequestParam("file") MultipartFile file,
      @PathVariable("id") UUID id,
      HttpServletResponse response) {
    ModelAndView mv = new ModelAndView();

    if (new ImageFileValidator().validate(file) instanceof FailedValidation) {
      response.addHeader("HX-Reswap", "afterbegin");
      response.addHeader("HX-Retarget", "#alerts");
      mv.setStatus(HttpStatus.FORBIDDEN);
      mv.setViewName("group-details/failed-to-edit-group-avatar");
      return mv;
    }

    try {
      this.imageFacade.setGroupAvatar(id, new ImageFile(file));

      mv.setViewName("group-details/page :: group-avatar");
      mv.addObject("groupId", id);
      mv.addObject("random", Math.random());

      var group = this.groupFacade.getWithMembers(id);
      if (group.isEmpty()) {
        throw new RuntimeException();
      }

      boolean canEditImages = false;
      if (SecurityContextHolder.getContext().getAuthentication()
          instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        canEditImages =
            group.get().groupMembers().stream()
                .anyMatch(
                    groupMember ->
                        groupMember
                            .user()
                            .id()
                            .equals(
                                UUID.fromString(usernamePasswordAuthenticationToken.getName())));
      }

      mv.addObject("canEditImages", canEditImages);
    } catch (ImageService.ImageCouldNotBeSavedException e) {
      response.addHeader("HX-Reswap", "afterbegin");
      response.addHeader("HX-Retarget", "#alerts");
      mv.setStatus(HttpStatus.FORBIDDEN);
      mv.setViewName("group-details/failed-to-edit-group-avatar");
    }

    return mv;
  }

  @PutMapping("/groups/banner/{id}")
  public ModelAndView editGroupBanner(
      @RequestParam("file") MultipartFile file,
      @PathVariable("id") UUID id,
      HttpServletResponse response) {
    ModelAndView mv = new ModelAndView();

    if (new ImageFileValidator().validate(file) instanceof FailedValidation) {
      response.addHeader("HX-Reswap", "afterbegin");
      response.addHeader("HX-Retarget", "#alerts");
      mv.setStatus(HttpStatus.FORBIDDEN);
      mv.setViewName("group-details/failed-to-edit-group-banner");
      return mv;
    }

    try {
      this.imageFacade.setGroupBanner(id, new ImageFile(file));

      mv.setViewName("group-details/page :: group-banner");
      mv.addObject("groupId", id);
      mv.addObject("random", Math.random());

      var group = this.groupFacade.getWithMembers(id);
      if (group.isEmpty()) {
        throw new RuntimeException();
      }

      boolean canEditImages = false;
      if (SecurityContextHolder.getContext().getAuthentication()
          instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        canEditImages =
            group.get().groupMembers().stream()
                .anyMatch(
                    groupMember ->
                        groupMember
                            .user()
                            .id()
                            .equals(
                                UUID.fromString(usernamePasswordAuthenticationToken.getName())));
      }

      mv.addObject("canEditImages", canEditImages);
    } catch (ImageService.ImageCouldNotBeSavedException e) {
      response.addHeader("HX-Reswap", "afterbegin");
      response.addHeader("HX-Retarget", "#alerts");
      mv.setStatus(HttpStatus.FORBIDDEN);
      mv.setViewName("group-details/failed-to-edit-group-banner");
    }

    return mv;
  }

  @DeleteMapping("/groups/avatar/{id}")
  public ModelAndView deleteGroupAvatar(@PathVariable("id") UUID id) {
    this.imageFacade.removeGroupAvatar(id);

    ModelAndView mv = new ModelAndView();

    mv.setViewName("group-details/page :: group-avatar");
    mv.addObject("groupId", id);
    mv.addObject("random", Math.random());

    return mv;
  }

  @DeleteMapping("/groups/banner/{id}")
  public ModelAndView deleteGroupBanner(@PathVariable("id") UUID id) {
    this.imageFacade.removeGroupBanner(id);
    ModelAndView mv = new ModelAndView();

    mv.setViewName("group-details/page :: group-banner");
    mv.addObject("groupId", id);
    mv.addObject("random", Math.random());

    return mv;
  }
}
