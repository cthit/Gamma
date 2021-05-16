package it.chalmers.gamma.internal.authority;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelFinder;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostDTO;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostFinder;
import it.chalmers.gamma.internal.authority.supergroup.service.AuthoritySuperGroupDTO;
import it.chalmers.gamma.internal.authority.supergroup.service.AuthoritySuperGroupFinder;
import it.chalmers.gamma.internal.authority.user.service.AuthorityUserDTO;
import it.chalmers.gamma.internal.authority.user.service.AuthorityUserFinder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/authority")
public class AuthorityAdminController {

    private final AuthorityPostFinder authorityPostFinder;
    private final AuthorityUserFinder authorityUserFinder;
    private final AuthoritySuperGroupFinder authoritySuperGroupFinder;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityAdminController(AuthorityPostFinder authorityPostFinder,
                                    AuthorityUserFinder authorityUserFinder,
                                    AuthoritySuperGroupFinder authoritySuperGroupFinder,
                                    AuthorityLevelFinder authorityLevelFinder) {
        this.authorityPostFinder = authorityPostFinder;
        this.authorityUserFinder = authorityUserFinder;
        this.authoritySuperGroupFinder = authoritySuperGroupFinder;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    private record Authorities(List<AuthorityPostDTO> postAuthorities,
                                      List<AuthoritySuperGroupDTO> superGroupAuthorities,
                                      List<AuthorityUserDTO> userAuthorities) { }

    @GetMapping
    public Map<AuthorityLevelName, Authorities> getAuthorities() {
        Map<AuthorityLevelName, Authorities> output = new HashMap<>();
        this.authorityLevelFinder.getAll()
                .forEach(authorityLevelName -> output.put(
                        authorityLevelName,
                        new Authorities(
                                Collections.emptyList(),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                ));

        this.authorityPostFinder.getAll()
                .forEach(authorityPost ->
                        output.get(authorityPost.authorityLevelName()).postAuthorities.add(authorityPost)
                );

        this.authorityUserFinder.getAll()
                .forEach(authorityUser ->
                        output.get(authorityUser.authorityLevelName()).userAuthorities.add(authorityUser)
                );

        this.authoritySuperGroupFinder.getAll()
                .forEach(authoritySuperGroup ->
                        output.get(authoritySuperGroup.authorityLevelName()).superGroupAuthorities.add(authoritySuperGroup)
                );

        return output;
    }

}
