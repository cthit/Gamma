package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static it.chalmers.gamma.DomainUtils.chair;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostRepositoryAdapter.class,
        PostEntityConverter.class})
public class PostEntityIntegrationTests {

    @Autowired
    private PostRepositoryAdapter postRepositoryAdapter;

    @Test
    public void Given_ValidPost_Expect_save_To_Work() {
        this.postRepositoryAdapter.save(chair);

        assertThat(this.postRepositoryAdapter.get(chair.id()))
                .get().isEqualTo(chair);
    }

    @Test
    public void Given_SamePost_Expect_save_To_Throw() {
        this.postRepositoryAdapter.save(chair);
        this.postRepositoryAdapter.save(chair);
    }

    //TODO: what if version != 0. postid doesn't exist.

}
