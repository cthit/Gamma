package it.chalmers.gamma.domain.post.controller.request;

import it.chalmers.gamma.domain.post.EmailPrefix;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import javax.validation.Valid;

public class AddPostRequest {

    @Valid
    public TextDTO post;

    @Valid
    public EmailPrefix emailPrefix;

}
