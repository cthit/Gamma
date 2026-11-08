package it.chalmers.gamma.adapter.secondary.webauthn;

import it.chalmers.gamma.app.signin.domain.Passkey;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.stereotype.Service;

@Service
public class PasskeyRepositoryAdapter implements PasskeyRepository {

  private final PublicKeyCredentialUserEntityRepository userEntities;
  private final UserCredentialRepository userCredentials;

  public PasskeyRepositoryAdapter(
      PublicKeyCredentialUserEntityRepository userEntities,
      UserCredentialRepository userCredentials) {
    this.userEntities = userEntities;
    this.userCredentials = userCredentials;
  }

  @Override
  public List<Passkey> getAll(UserId userId) {
    return credentials(userId).stream()
        .sorted(
            Comparator.comparing(
                CredentialRecord::getCreated, Comparator.nullsFirst(Comparator.naturalOrder())))
        .map(credential ->
            new Passkey(
                credential.getCredentialId().toBase64UrlString(),
                credential.getLabel(),
                credential.getCreated(),
                credential.getLastUsed()))
        .toList();
  }

  @Override
  public boolean hasAny(UserId userId) {
    return !credentials(userId).isEmpty();
  }

  @Override
  public boolean delete(UserId userId, String credentialId) {
    PublicKeyCredentialUserEntity userEntity = this.userEntities.findByUsername(name(userId));
    if (userEntity == null) {
      return false;
    }

    Bytes id;
    try {
      id = Bytes.fromBase64(credentialId);
    } catch (IllegalArgumentException e) {
      return false;
    }

    CredentialRecord credential = this.userCredentials.findByCredentialId(id);
    if (credential == null || !credential.getUserEntityUserId().equals(userEntity.getId())) {
      return false;
    }

    this.userCredentials.delete(id);
    return true;
  }

  @Override
  public void deleteAll(UserId userId) {
    PublicKeyCredentialUserEntity userEntity = this.userEntities.findByUsername(name(userId));
    if (userEntity != null) {
      this.userEntities.delete(userEntity.getId());
    }
  }

  private List<CredentialRecord> credentials(UserId userId) {
    PublicKeyCredentialUserEntity userEntity = this.userEntities.findByUsername(name(userId));
    if (userEntity == null) {
      return List.of();
    }
    return this.userCredentials.findByUserId(userEntity.getId());
  }

  private static String name(UserId userId) {
    return userId.value().toString();
  }
}
