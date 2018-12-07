package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.OAuth2User;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface OAuth2UserRepository extends Repository<OAuth2User, String>{
    OAuth2User findOneByNick(String nick);
}
