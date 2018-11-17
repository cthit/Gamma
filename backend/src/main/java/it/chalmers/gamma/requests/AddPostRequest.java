package it.chalmers.gamma.requests;

import it.chalmers.gamma.db.entity.Text;

public class AddPostRequest {
    private Text post;

    public Text getPost() {
        return post;
    }

    public void setPost(Text post) {
        this.post = post;
    }
}
