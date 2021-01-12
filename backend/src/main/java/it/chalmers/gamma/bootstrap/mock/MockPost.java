package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.text.Text;

import java.util.UUID;

public class MockPost {

    private UUID id;
    private Text postName;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Text getPostName() {
        return this.postName;
    }

    public void setPostName(Text postName) {
        this.postName = postName;
    }
}
