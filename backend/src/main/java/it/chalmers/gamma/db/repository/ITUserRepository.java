package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.ITUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ITUserRepository extends JpaRepository<ITUser, UUID> {

    public ITUser findByCid(String cid);

}
