package it.chalmers.gamma.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Year;

@RecordBuilder
public record User(UserId id,
                   Cid cid,
                   Email email,
                   Language language,
                   Nick nick,
                   FirstName firstName,
                   LastName lastName,
                   boolean userAgreement,
                   Year acceptanceYear,
                   boolean activated)
        implements DTO, UserBuilder.With { }
