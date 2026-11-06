package it.chalmers.gamma.bootstrap;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record MockData(
    List<MockUser> users,
    List<MockGroup> groups,
    List<MockSuperGroup> superGroups,
    List<MockPost> posts,
    List<MockClient> clients) {

  public MockData {
    // Mock files written before clients were added to the mock data have no clients section
    if (clients == null) {
      clients = Collections.emptyList();
    }
  }

  public static MockData empty() {
    return new MockData(
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList());
  }

  public record MockGroup(
      UUID id, String name, String prettyName, List<MockMembership> members, UUID superGroupId) {}

  public record MockMembership(UUID userId, UUID postId, String unofficialPostName) {}

  public record MockText(String sv, String en) {}

  public record MockPost(UUID id, MockText postName) {}

  public record MockSuperGroup(
      UUID id, String name, String prettyName, String type, List<String> authorities) {}

  public record MockUser(
      UUID id,
      String cid,
      String nick,
      String firstName,
      String lastName,
      int acceptanceYear,
      Boolean admin) {}

  /**
   * A client is official if ownerId is null, otherwise it is owned by the user with that id.
   * clientSecret and apiKey.token are the raw values, they are encoded when the client is created.
   */
  public record MockClient(
      UUID id,
      String clientId,
      String clientSecret,
      String redirectUrl,
      String prettyName,
      MockText description,
      boolean emailScope,
      UUID ownerId,
      MockApiKey apiKey,
      List<MockClientAuthority> authorities) {}

  public record MockApiKey(UUID id, String token) {}

  public record MockClientAuthority(String name, List<UUID> superGroupIds, List<UUID> userIds) {}
}
