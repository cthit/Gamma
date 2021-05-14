package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityFinder implements GetAllEntities<AuthorityLevelName> {

    //return all from post, supergroup and user
    @Override
    public List<AuthorityLevelName> getAll() {
        return null;
    }
}
