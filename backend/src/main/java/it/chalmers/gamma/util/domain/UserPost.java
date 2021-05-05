package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.domain.post.service.PostDTO;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;

public record UserPost(UserRestrictedDTO user,
                       PostDTO post) {

}
