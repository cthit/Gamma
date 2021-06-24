package it.chalmers.gamma.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.util.entity.DTO;

@RecordBuilder
public record User(UserId id,
                   Cid cid,
                   Email email,
                   Language language,
                   Nick nick,
                   FirstName firstName,
                   LastName lastName,
                   boolean userAgreement,
                   AcceptanceYear acceptanceYear)
        implements DTO, UserBuilder.With { }
