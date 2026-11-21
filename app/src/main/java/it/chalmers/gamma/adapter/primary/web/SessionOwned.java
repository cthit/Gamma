package it.chalmers.gamma.adapter.primary.web;

import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

record SessionOwned<T extends Serializable>(String owner, T value) implements Serializable {

  static String currentOwner() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("Only signed in users have pending sign in changes");
    }
    return authentication.getName();
  }

  static <T extends Serializable> Optional<T> get(
      HttpSession session, String attribute, Class<T> type) {
    if (!(session.getAttribute(attribute) instanceof SessionOwned<?> owned)
        || !type.isInstance(owned.value())) {
      return Optional.empty();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !Objects.equals(authentication.getName(), owned.owner())) {
      session.removeAttribute(attribute);
      return Optional.empty();
    }

    return Optional.of(type.cast(owned.value()));
  }
}
