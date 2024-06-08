package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.secondary.image.ImageFile;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.image.domain.ImageService;
import java.util.UUID;
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
      @RequestParam("file") MultipartFile file, @PathVariable("id") UUID id) {
    try {
      this.imageFacade.setGroupAvatar(id, new ImageFile(file));
    } catch (ImageService.ImageCouldNotBeSavedException e) {
      throw new RuntimeException(e);
    }

    ModelAndView mv = new ModelAndView();

    mv.setViewName("pages/group-details :: group-avatar");
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
                          .equals(UUID.fromString(usernamePasswordAuthenticationToken.getName())));
    }

    mv.addObject("canEditImages", canEditImages);

    return mv;
  }

  @PutMapping("/groups/banner/{id}")
  public ModelAndView editGroupBanner(
      @RequestParam("file") MultipartFile file, @PathVariable("id") UUID id) {
    try {
      this.imageFacade.setGroupBanner(id, new ImageFile(file));
    } catch (ImageService.ImageCouldNotBeSavedException e) {
      throw new RuntimeException(e);
    }

    ModelAndView mv = new ModelAndView();

    mv.setViewName("pages/group-details :: group-banner");
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
                          .equals(UUID.fromString(usernamePasswordAuthenticationToken.getName())));
    }

    mv.addObject("canEditImages", canEditImages);

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
