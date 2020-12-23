package it.chalmers.gamma.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITUserRepository extends JpaRepository<ITUser, UUID> {

    Optional<ITUser> findByCid(String cid);

    Optional<ITUser> findByEmail(String email);

    boolean existsByCid(String cid);

}
