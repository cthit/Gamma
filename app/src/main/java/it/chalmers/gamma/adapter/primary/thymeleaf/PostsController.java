package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.post.PostFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PostsController {

    private final PostFacade postFacade;

    public PostsController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @GetMapping("/posts")
    public ModelAndView getPosts(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<PostFacade.PostDTO> posts = postFacade.getAll();

        var mv = Page.SHOW_POSTS.create(htmxRequest);
        mv.addObject("posts", posts);

        return mv;
    }

}
