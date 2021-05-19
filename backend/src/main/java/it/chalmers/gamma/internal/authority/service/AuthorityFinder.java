package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostFinder;
import it.chalmers.gamma.internal.authority.supergroup.service.AuthoritySuperGroupFinder;
import it.chalmers.gamma.internal.authority.user.service.AuthorityUserFinder;
import org.springframework.stereotype.Service;

@Service
public class AuthorityFinder {

    private final AuthorityPostFinder authorityPostFinder;
    private final AuthorityUserFinder authorityUserFinder;
    private final AuthoritySuperGroupFinder authoritySuperGroupFinder;

    public AuthorityFinder(AuthorityPostFinder authorityPostFinder,
                           AuthorityUserFinder authorityUserFinder,
                           AuthoritySuperGroupFinder authoritySuperGroupFinder) {
        this.authorityPostFinder = authorityPostFinder;
        this.authorityUserFinder = authorityUserFinder;
        this.authoritySuperGroupFinder = authoritySuperGroupFinder;
    }

    public boolean authorityLevelUsed(AuthorityLevelName name) {
        return this.authorityPostFinder.existsBy(name)
                && this.authorityUserFinder.existsBy(name)
                && this.authoritySuperGroupFinder.existsBy(name);
    }

}
