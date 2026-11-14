package it.chalmers.gamma.adapter.secondary.jpa.datadeletion;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRecipientRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataDeletionRecipientRepositoryAdapter implements DataDeletionRecipientRepository {

  private final DataDeletionRecipientJpaRepository repository;

  public DataDeletionRecipientRepositoryAdapter(DataDeletionRecipientJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void add(Email email) throws RecipientAlreadyExistsException {
    String normalized = normalize(email);
    if (this.repository.existsById(normalized)) {
      throw new RecipientAlreadyExistsException();
    }

    this.repository.save(new DataDeletionRecipientEntity(normalized));
  }

  @Override
  public void remove(Email email) throws RecipientNotFoundException {
    String normalized = normalize(email);
    if (!this.repository.existsById(normalized)) {
      throw new RecipientNotFoundException();
    }

    this.repository.deleteById(normalized);
  }

  @Override
  public List<Email> getAll() {
    return this.repository.findAll().stream()
        .map(DataDeletionRecipientEntity::getId)
        .sorted()
        .map(Email::new)
        .toList();
  }

  private static String normalize(Email email) {
    return email.value().trim().toLowerCase();
  }
}
