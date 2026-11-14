package it.chalmers.gamma.security;

import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Set;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionRevocationService {
  private final FindByIndexNameSessionRepository<? extends Session> sessions;

  public SessionRevocationService(FindByIndexNameSessionRepository<? extends Session> sessions) {
    this.sessions = sessions;
  }

  public void revokeAll(UserId userId) {
    scrubAndDelete(this.sessions, userId.value().toString());
  }

  private static <S extends Session> void scrubAndDelete(
      FindByIndexNameSessionRepository<S> sessions, String principal) {
    for (var entry : sessions.findByPrincipalName(principal).entrySet()) {
      S session = entry.getValue();
      // Indexed Redis sessions keep expired hashes briefly for lifecycle events.
      // Remove the identity, security context and saved requests before expiring them.
      Set.copyOf(session.getAttributeNames()).forEach(session::removeAttribute);
      sessions.save(session);
      sessions.deleteById(entry.getKey());
    }
  }
}
