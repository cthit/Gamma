package it.chalmers.gamma.adapter.secondary.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

//TODO: Only UserRepositoryAdapter and UserPasswordRetrieverAdapter should be able to access this.
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByCid(String cid);

    Optional<UserEntity> findByEmail(String email);

}