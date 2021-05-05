package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.internal.post.service.PostDTO;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;

public record UserPost(UserRestrictedDTO user,
                       PostDTO post) {

}
