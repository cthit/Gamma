package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

import java.util.Calendar;
import java.util.Objects;
public record GroupShallowDTO(GroupId id,
                          Calendar becomesActive,
                          Calendar becomesInactive,
                          Email email,
                          String name,
                          String prettyName,
                          SuperGroupId superGroupId) implements DTO {
}
