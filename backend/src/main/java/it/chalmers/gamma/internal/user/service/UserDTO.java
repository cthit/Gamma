package it.chalmers.gamma.internal.user.service;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;

import java.time.Year;

@RecordBuilder
public record UserDTO(UserId id,
                      Cid cid,
                      Email email,
                      Language language,
                      Nick nick,
                      FirstName firstName,
                      LastName lastName,
                      boolean userAgreement,
                      Year acceptanceYear,
                      boolean activated)
        implements DTO, UserDTOBuilder.With { }
