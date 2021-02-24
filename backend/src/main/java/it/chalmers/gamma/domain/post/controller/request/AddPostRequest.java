package it.chalmers.gamma.domain.post.controller.request;

import it.chalmers.gamma.domain.text.data.db.Text;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import java.util.Objects;
import javax.validation.constraints.NotNull;


public class AddPostRequest {

    public TextDTO post;
    public String emailPrefix;

}
