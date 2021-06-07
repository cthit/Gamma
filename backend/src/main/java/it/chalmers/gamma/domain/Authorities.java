package it.chalmers.gamma.domain;


import java.util.List;

public record Authorities(List<AuthorityPost> postAuthorities,
                           List<AuthoritySuperGroup> superGroupAuthorities,
                           List<AuthorityUser> userAuthorities) { }