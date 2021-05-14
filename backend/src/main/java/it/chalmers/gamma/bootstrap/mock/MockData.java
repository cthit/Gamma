package it.chalmers.gamma.bootstrap.mock;

import java.util.List;

public record MockData(List<MockUser> users,
                       List<MockGroup> groups,
                       List<MockSuperGroup> superGroups,
                       List<MockPost> posts) {

}


