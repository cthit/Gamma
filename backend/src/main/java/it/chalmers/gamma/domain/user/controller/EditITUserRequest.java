package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;

public class EditITUserRequest {

    public String nick;
    public String firstName;
    public String lastName;
    public Email email;
    public Language language;
    public int acceptanceYear;

}
