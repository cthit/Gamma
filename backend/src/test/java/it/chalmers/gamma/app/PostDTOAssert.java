package it.chalmers.gamma.app;

import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.Post;
import org.assertj.core.api.AbstractAssert;

public class PostDTOAssert extends AbstractAssert<PostDTOAssert, PostFacade.PostDTO> {

    protected PostDTOAssert(PostFacade.PostDTO postDTO) {
        super(postDTO, PostDTOAssert.class);
    }

    public static PostDTOAssert assertThat(PostFacade.PostDTO postDTO) {
        return new PostDTOAssert(postDTO);
    }

    public PostDTOAssert isEqualTo(Post post) {
        isNotNull();

        throw new UnsupportedOperationException();
    }

}
