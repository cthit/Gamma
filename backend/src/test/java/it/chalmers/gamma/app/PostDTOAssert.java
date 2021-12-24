package it.chalmers.gamma.app;

import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.Post;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class PostDTOAssert extends AbstractAssert<PostDTOAssert, PostFacade.PostDTO> {

    protected PostDTOAssert(PostFacade.PostDTO postDTO) {
        super(postDTO, PostDTOAssert.class);
    }

    public static PostDTOAssert assertThat(PostFacade.PostDTO postDTO) {
        return new PostDTOAssert(postDTO);
    }

    public PostDTOAssert isEqualTo(Post post) {
        isNotNull();

        Assertions.assertThat(actual)
                .hasOnlyFields("id", "version", "svName", "enName", "emailPrefix")
                .isEqualTo(new PostFacade.PostDTO(
                        post.id().value(),
                        post.version(),
                        post.name().sv().value(),
                        post.name().en().value(),
                        post.emailPrefix().value()
                ));

        return this;
    }

}
