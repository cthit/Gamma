package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.AuthorityPK;
import it.chalmers.gamma.db.repository.AuthorityRepository;
import org.codehaus.jackson.schema.JsonSerializableSchema;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository){
        this.authorityRepository = authorityRepository;
    }

    public void setAuthorityLevel(FKITGroup group, Post post, Authority.Authorities authorityLevel){
        Authority authority = authorityRepository.findById_FkitGroupAndAndId_Post(group, post);
        if(authority == null) {
            authority = new Authority();
            AuthorityPK pk = new AuthorityPK();
            pk.setFkitGroup(group);
            pk.setPost(post);
            authority.setId(pk);
        }
        authority.setAuthorityLevel(authorityLevel);
        authorityRepository.save(authority);
    }

    public Authority getAuthorityLevel(FKITGroup group, Post post){
        return authorityRepository.findById_FkitGroupAndAndId_Post(group, post);
    }

    public void removeAuthority(FKITGroup group, Post post){
        Authority authority = authorityRepository.findById_FkitGroupAndAndId_Post(group, post);
        authorityRepository.delete(authority);
    }


}
