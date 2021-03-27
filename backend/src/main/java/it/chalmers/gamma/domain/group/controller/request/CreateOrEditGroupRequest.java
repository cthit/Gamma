package it.chalmers.gamma.domain.group.controller.request;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import java.util.Calendar;

public class CreateOrEditGroupRequest {

    public String name;
    public String prettyName;
    public String avatarURL;
    public Calendar becomesActive;
    public Calendar becomesInactive;
    public SuperGroupId superGroup;
    public Email email;

}
