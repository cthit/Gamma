package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.Calendar;
import java.util.GregorianCalendar;

public record GroupDTO(GroupId id,
                   Email email,
                   String name,
                   String prettyName,
                   SuperGroupDTO superGroup)
        implements DTO { }
