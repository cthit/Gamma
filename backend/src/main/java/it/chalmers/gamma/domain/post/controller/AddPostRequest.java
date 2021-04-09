package it.chalmers.gamma.domain.post.controller;

import it.chalmers.gamma.domain.post.service.EmailPrefix;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import javax.validation.Valid;

public class AddPostRequest {

    @Valid
    protected TextDTO post;

    @Valid
    protected EmailPrefix emailPrefix;

    protected AddPostRequest() { }

}
