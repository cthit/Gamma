package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.web.servlet.ModelAndView;

import static it.chalmers.gamma.adapter.primary.thymeleaf.PageAuth.ADMIN;
import static it.chalmers.gamma.adapter.primary.thymeleaf.PageAuth.SIGNED_IN;

public enum Page {
    HOME("pages/home", SIGNED_IN),
    SHOW_SUPER_GROUPS("pages/show-super-groups", SIGNED_IN),
    SHOW_SUPER_GROUP_TYPES("pages/show-types", SIGNED_IN),
    SHOW_POSTS("pages/show-posts", SIGNED_IN),
    SHOW_USERS("pages/show-users", SIGNED_IN),
    SHOW_GROUPS("pages/show-groups", SIGNED_IN),
    SHOW_ACTIVATION_CODES("pages/show-activation-codes", ADMIN),
    SHOW_CLIENTS("pages/show-clients", ADMIN),
    ADMINS("pages/admins", ADMIN),
    GDPR("pages/gdpr", ADMIN),
    SHOW_API_KEYS("pages/show-api-keys", ADMIN);

    Page(String fragment, PageAuth auth) {
        this.FRAGMENT = fragment;
        this.AUTH = auth;
    }

    public final String FRAGMENT;
    public final PageAuth AUTH;

    public ModelAndView create(boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();

        if(htmxRequest) {
            mv.setViewName(this.FRAGMENT);
        } else {
            mv.setViewName("index");
            mv.addObject("selectedPage", this);
        }

        return mv;
    }

}
