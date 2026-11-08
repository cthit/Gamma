package it.chalmers.gamma.adapter.secondary.jpa.datadeletion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataDeletionRecipientJpaRepository
    extends JpaRepository<DataDeletionRecipientEntity, String> {}
