package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.text.Text;

import java.util.UUID;

public class MockPost {

    private PostId id;
    private Text postName;

    public PostId getId() {
        return this.id;
    }

    public void setId(PostId id) {
        this.id = id;
    }

    public Text getPostName() {
        return this.postName;
    }

    public void setPostName(Text postName) {
        this.postName = postName;
    }
}
