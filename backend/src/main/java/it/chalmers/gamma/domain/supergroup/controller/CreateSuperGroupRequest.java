package it.chalmers.gamma.domain.supergroup.controller;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupType;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CreateSuperGroupRequest {

    @NotNull(message = "NAME_MUST_BE_PROVIDED")
    @Size(min = 2, max = 50, message = "NAME_MUST_BE_BETWEEN_2_AND_50_CHARACTERS")
    protected String name;

    @Size(max = 50, message = "PRETTY_NAME_TOO_LONG")
    protected String prettyName;

    @NotNull(message = "TYPE_MUST_BE_PROVIDED")
    protected SuperGroupType type;

    @NotNull(message = "EMAIL_MUST_BE_PROVIDED")
    protected Email email;

    protected TextDTO description;

    protected CreateSuperGroupRequest() { }

}
