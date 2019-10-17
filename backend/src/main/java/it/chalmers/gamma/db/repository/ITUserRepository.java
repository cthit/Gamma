package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.ITUser;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITUserRepository extends JpaRepository<ITUser, UUID> {

    ITUser findByCid(String cid);

    ITUser findByEmail(String email);

    boolean existsByCid(String cid);

}
