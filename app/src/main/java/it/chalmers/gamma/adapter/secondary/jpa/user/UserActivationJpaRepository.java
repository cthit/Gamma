package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivationJpaRepository extends JpaRepository<UserActivationEntity, String> {

  Optional<UserActivationEntity> findByToken(String token);
}
