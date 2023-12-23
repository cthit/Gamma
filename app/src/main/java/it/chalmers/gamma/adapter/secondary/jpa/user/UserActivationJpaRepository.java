package it.chalmers.gamma.adapter.secondary.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActivationJpaRepository extends JpaRepository<UserActivationEntity, String> {

    Optional<UserActivationEntity> findByToken(String token);

}
