package it.chalmers.gamma.util.mock;

import it.chalmers.gamma.db.entity.Text;

import java.util.UUID;

public class MockPost {

    private UUID id;
    private Text postName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Text getPostName() {
        return postName;
    }

    public void setPostName(Text postName) {
        this.postName = postName;
    }
}
