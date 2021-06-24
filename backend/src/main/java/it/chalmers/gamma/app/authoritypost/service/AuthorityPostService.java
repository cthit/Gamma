package it.chalmers.gamma.app.authoritypost.service;

import it.chalmers.gamma.app.domain.Authorities;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.AuthorityPost;
import it.chalmers.gamma.app.post.service.PostService;
import it.chalmers.gamma.app.supergroup.service.SuperGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorityPostService {

    private final AuthorityPostRepository repository;
    private final SuperGroupService superGroupService;
    private final PostService postService;

    public AuthorityPostService(AuthorityPostRepository repository,
                                SuperGroupService superGroupService,
                                PostService postService) {
        this.repository = repository;
        this.superGroupService = superGroupService;
        this.postService = postService;
    }

    public void create(AuthorityPostShallowDTO authority) throws AuthorityPostNotFoundException {
        try {
            this.repository.save(
                    new AuthorityPostEntity(authority)
            );
        } catch(IllegalArgumentException e) {
            throw new AuthorityPostNotFoundException();
        }
    }

    public void delete(AuthorityPostPK id) throws AuthorityPostNotFoundException {
        try{
            this.repository.deleteById(id);
        } catch(IllegalArgumentException e){
            throw new AuthorityPostNotFoundException();
        }
    }

    public List<AuthorityPost> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(AuthorityPostEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<Authorities.SuperGroupPost> getByAuthorityLevel(AuthorityLevelName authorityLevelName) {
        return this.repository.findAuthoritiesById_AuthorityLevelName(authorityLevelName)
                .stream()
                .map(AuthorityPostEntity::toDTO)
                .map(this::fromShallow)
                .map(authorityPost -> new Authorities.SuperGroupPost(authorityPost.superGroup(), authorityPost.post()))
                .collect(Collectors.toList());
    }

    public boolean existsBy(AuthorityLevelName name) {
        return this.repository.existsById_AuthorityLevelName(name);
    }

    private AuthorityPost fromShallow(AuthorityPostShallowDTO authority) {
        try {
            return new AuthorityPost(
                    this.superGroupService.get(authority.superGroupId()),
                    this.postService.get(authority.postId()),
                    authority.authorityLevelName()
            );
        } catch (PostService.PostNotFoundException | SuperGroupService.SuperGroupNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class AuthorityPostNotFoundException extends Exception { }

}
