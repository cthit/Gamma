package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.PostRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PostsController {

    private final PostFacade postFacade;

    public PostsController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @GetMapping("/posts")
    public ModelAndView getPosts(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<PostFacade.PostDTO> posts = postFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/posts");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/posts");
        }

        mv.addObject("posts", posts);

        return mv;
    }

    @GetMapping("/posts/{id}")
    public ModelAndView getPost(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, @PathVariable("id") UUID postId) {
        Optional<PostFacade.PostDTO> post = postFacade.get(postId);

        if(post.isEmpty()) {
            throw new IllegalArgumentException("Not found");
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("pages/posts :: post-row");
        mv.addObject("post", post.get());

        return mv;
    }

    @GetMapping("/posts/{id}/edit")
    public ModelAndView getEditPost(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, @PathVariable("id") UUID postId) {
        Optional<PostFacade.PostDTO> post = postFacade.get(postId);

        if(post.isEmpty()) {
            throw new IllegalArgumentException("Not found");
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("partial/edit-post");
        mv.addObject("post", post.get());

        return mv;
    }

    @PutMapping(value = "/posts/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView updateEditPost(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, @PathVariable("id") UUID postId, UpdatePost form) {
        System.out.println(form);

        try {
            postFacade.update(new PostFacade.UpdatePost(
                    postId,
                    form.version,
                    form.svName,
                    form.enName,
                    form.emailPrefix
            ));
        } catch (PostRepository.PostNotFoundException e) {
            throw new RuntimeException(e);
        }

        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/posts :: post-row");
        mv.addObject("post", new PostFacade.PostDTO(
                postId,
                form.version + 1,
                form.svName,
                form.enName,
                form.emailPrefix
        ));

        System.out.println(mv);

        return mv;
    }

    public record UpdatePost(
            String svName,
            String enName,
            String emailPrefix,
            int version
    ) { }

    @PostMapping("/posts")
    public ModelAndView updateEditPost(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, CreatePost form) {
        UUID postId = postFacade.create(new PostFacade.NewPost(
                form.svName,
                form.enName,
                form.emailPrefix
        ));

        ModelAndView mv = new ModelAndView();

        mv.setViewName("partial/created-post");
        mv.addObject("post", new PostFacade.PostDTO(
                postId,
                0,
                form.svName,
                form.enName,
                form.emailPrefix
        ));

        return mv;
    }

    public record CreatePost(
            String svName,
            String enName,
            String emailPrefix
    ) {

    }

    @DeleteMapping(value = "/posts/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String deleteEditPost(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, @PathVariable("id") UUID postId) {
        try {
            postFacade.delete(postId);
        } catch (PostRepository.PostNotFoundException e) {
            throw new RuntimeException(e);
        }

        return "common/empty";
    }

}
