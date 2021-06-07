package it.chalmers.gamma.internal.authoritypost.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.AuthorityPost;
import it.chalmers.gamma.internal.post.service.PostService;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
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

    public List<AuthorityPost> getByAuthorityLevel(AuthorityLevelName authorityLevelName) {
        return this.repository.findAuthoritiesById_AuthorityLevelName(authorityLevelName)
                .stream()
                .map(AuthorityPostEntity::toDTO)
                .map(this::fromShallow)
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
