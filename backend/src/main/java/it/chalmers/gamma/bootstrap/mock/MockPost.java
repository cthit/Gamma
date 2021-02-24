package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.text.data.db.Text;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public class MockPost {

    private PostId id;
    private TextDTO postName;

    public PostId getId() {
        return this.id;
    }

    public void setId(PostId id) {
        this.id = id;
    }

    public TextDTO getPostName() {
        return this.postName;
    }

    public void setPostName(TextDTO postName) {
        this.postName = postName;
    }
}
