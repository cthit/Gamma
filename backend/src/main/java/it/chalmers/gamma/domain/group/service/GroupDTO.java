package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.util.Calendar;
import java.util.GregorianCalendar;

public record GroupDTO(GroupId id,
                   Calendar becomesActive,
                   Calendar becomesInactive,
                   Email email,
                   String name,
                   String prettyName,
                   SuperGroupDTO superGroup)
        implements DTO {
    public boolean isActive() {
        Calendar now = new GregorianCalendar();
        return now.after(this.becomesActive) && now.before(this.becomesInactive);
    }
}
