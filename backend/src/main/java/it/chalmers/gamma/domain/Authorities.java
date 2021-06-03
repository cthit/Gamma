package it.chalmers.gamma.domain;


import it.chalmers.gamma.internal.authority.post.service.AuthorityPostDTO;
import it.chalmers.gamma.internal.authority.supergroup.service.AuthoritySuperGroupDTO;
import it.chalmers.gamma.internal.authority.user.service.AuthorityUserDTO;

import java.util.List;

public record Authorities(List<AuthorityPostDTO> postAuthorities,
                           List<AuthoritySuperGroupDTO> superGroupAuthorities,
                           List<AuthorityUserDTO> userAuthorities) { }