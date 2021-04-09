package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

import java.util.Calendar;

public class CreateOrEditGroupRequest {

    protected CreateOrEditGroupRequest() { }

    protected String name;
    protected String prettyName;
    protected String avatarURL;
    protected Calendar becomesActive;
    protected Calendar becomesInactive;
    protected SuperGroupId superGroup;
    protected Email email;

}
