package it.chalmers.gamma.domain.user.controller;

import javax.validation.Valid;

public class AdminChangePasswordRequest {
    @Valid
    public String password;
}
