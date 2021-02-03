package it.chalmers.gamma.user.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByCid(String cid);

    Optional<User> findByEmail(String email);

    boolean existsByCid(String cid);

}
