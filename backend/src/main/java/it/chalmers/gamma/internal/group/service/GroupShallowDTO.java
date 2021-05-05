package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

import java.util.Calendar;

public record GroupShallowDTO(GroupId id,
                          Calendar becomesActive,
                          Calendar becomesInactive,
                          Email email,
                          String name,
                          String prettyName,
                          SuperGroupId superGroupId) implements DTO {
}
