package it.chalmers.gamma.internal.user.service;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;

import java.time.Year;

@RecordBuilder
public record UserDTO(UserId id,
               Cid cid,
               Email email,
               Language language,
               String nick,
               String firstName,
               String lastName,
               boolean userAgreement,
               Year acceptanceYear,
               boolean activated)
        implements DTO, UserDTOBuilder.With { }
