package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;
import static it.chalmers.gamma.app.validation.ValidationHelper.*;
import static it.chalmers.gamma.app.validation.ValidationHelper.MAX_LENGTH;

import it.chalmers.gamma.app.common.UUIDValidator;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.validation.ValidationResult;
import it.chalmers.gamma.app.validation.Validator;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PostsController {

  private final PostFacade postFacade;

  public PostsController(PostFacade postFacade) {
    this.postFacade = postFacade;
  }

  @GetMapping("/posts")
  public ModelAndView getPosts(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<PostFacade.PostDTO> posts = postFacade.getAll();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("posts/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "posts/page");
    }

    mv.addObject(
        "posts",
        posts.stream().sorted(Comparator.comparing(post -> post.enName().toLowerCase())).toList());

    return mv;
  }

  private ModelAndView createPostNotFound(String postId, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("post-details/not-found");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "post-details/not-found");
    }

    mv.addObject("id", postId);

    return mv;
  }

  public record PostUsage(UUID groupId, String groupPrettyName, UUID userId, String username) {}

  private ModelAndView createGetPost(String postId, boolean htmxRequest) {
    if (!UUIDValidator.isValidUUID(postId)) {
      return createPostNotFound(postId, htmxRequest);
    }

    UUID id = UUID.fromString(postId);

    Optional<PostFacade.PostDTO> post = postFacade.get(id);

    if (post.isEmpty()) {
      return createPostNotFound(postId, htmxRequest);
    }

    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("post-details/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "post-details/page");
    }

    mv.addObject("post", post.get());

    List<PostUsage> usages = new ArrayList<>();
    for (GroupFacade.GroupWithMembersDTO postUsage : postFacade.getPostUsages(post.get().id())) {
      for (GroupFacade.GroupMemberDTO user : postUsage.groupMembers()) {
        if (user.post().id().equals(id)) {
          usages.add(
              new PostUsage(
                  postUsage.id(),
                  postUsage.prettyName(),
                  user.user().id(),
                  user.user().fullName()));
        }
      }
    }

    mv.addObject("usages", usages);

    return mv;
  }

  @GetMapping("/posts/{id}")
  public ModelAndView getPost(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") String postId) {

    return createGetPost(postId, htmxRequest);
  }

  private ModelAndView createGetEditPost(
      boolean htmxRequest, UUID postId, UpdatePost form, final BindingResult bindingResult) {
    Optional<PostFacade.PostDTO> post = postFacade.get(postId);

    if (post.isEmpty()) {
      throw new IllegalArgumentException("Not found");
    }

    ModelAndView mv = new ModelAndView();

    if (form == null) {
      form =
          new UpdatePost(
              post.get().svName(),
              post.get().enName(),
              post.get().emailPrefix(),
              post.get().version());
    }

    if (bindingResult != null) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    mv.addObject("form", form);
    mv.addObject("postId", postId);

    if (htmxRequest) {
      mv.setViewName("post-details/edit-post");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "post-details/edit-post");
    }

    return mv;
  }

  @GetMapping("/posts/{id}/edit")
  public ModelAndView getEditPost(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID postId) {
    return createGetEditPost(htmxRequest, postId, null, null);
  }

  @PutMapping(value = "/posts/{id}")
  public ModelAndView updateEditPost(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") UUID postId,
      HttpServletResponse response,
      UpdatePost form,
      final BindingResult bindingResult) {

    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      response.addHeader("HX-Reswap", "outerHTML");
      response.addHeader("HX-Retarget", "closest <article/>");
      return createGetEditPost(htmxRequest, postId, form, bindingResult);
    }

    try {
      postFacade.update(
          new PostFacade.UpdatePost(
              postId, form.version, form.svName, form.enName, form.emailPrefix));
    } catch (PostRepository.PostNotFoundException e) {
      throw new RuntimeException(e);
    }

    return new ModelAndView("redirect:/posts/" + postId.toString());
  }

  public record UpdatePost(
      @ValidatedWith(NameValidator.class) String svName,
      @ValidatedWith(NameValidator.class) String enName,
      @ValidatedWith(EmailPrefix.EmailPrefixValidator.class) String emailPrefix,
      int version) {}

  @DeleteMapping(value = "/posts/{id}", produces = MediaType.TEXT_HTML_VALUE)
  public String deleteEditPost(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID postId) {
    try {
      postFacade.delete(postId);
    } catch (PostRepository.PostNotFoundException e) {
      throw new RuntimeException(e);
    }

    return "redirect:/posts";
  }

  public record CreatePost(
      @ValidatedWith(NameValidator.class) String svName,
      @ValidatedWith(NameValidator.class) String enName,
      @ValidatedWith(EmailPrefix.EmailPrefixValidator.class) String emailPrefix) {}

  public static final class NameValidator implements Validator<String> {
    @Override
    public ValidationResult validate(String value) {
      return withValidators(IS_NOT_EMPTY, SANITIZED_HTML, MAX_LENGTH.apply(2048)).validate(value);
    }
  }

  private ModelAndView createGetCreatePost(
      boolean htmxRequest, CreatePost form, BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("create-post/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "create-post/page");
    }

    if (form == null) {
      form = new CreatePost("", "", "");
    }

    if (bindingResult != null) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    mv.addObject("form", form);

    return mv;
  }

  @GetMapping("/posts/create")
  public ModelAndView getCreatePost(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return createGetCreatePost(htmxRequest, null, null);
  }

  @PostMapping("/posts")
  public ModelAndView createPost(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      CreatePost form,
      BindingResult bindingResult) {

    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      return createGetCreatePost(htmxRequest, form, bindingResult);
    }

    UUID postId =
        this.postFacade.create(new PostFacade.NewPost(form.svName, form.enName, form.emailPrefix));

    return new ModelAndView("redirect:/posts/" + postId);
  }
}
