package it.chalmers.gamma.Endoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.chalmers.gamma.Endoints.Method.DELETE;
import static it.chalmers.gamma.Endoints.Method.GET;
import static it.chalmers.gamma.Endoints.Method.POST;
import static it.chalmers.gamma.Endoints.Method.PUT;

public final class Endpoints {
    public static List<Endpoint> getNormalUserEndpoints() {
        return Arrays.asList(
                new Endpoint("/groups", GET),
                new Endpoint("/groups/active", GET),
                new Endpoint("/groups/minified", GET),
                new Endpoint("/groups/posts", GET),
                new Endpoint("/groups/posts/%s", GET),
                new Endpoint("/groups/%s", GET),
                new Endpoint("/groups/%s/members", GET),
                new Endpoint("/groups/%s/minified", GET),
                new Endpoint("/login", GET),
                new Endpoint("/superGroups", GET),
                new Endpoint("/superGroups/%s", GET),
                new Endpoint("/superGroups/%s/active", GET),
                new Endpoint("/superGroups/%s/subgroups", GET),
                new Endpoint("/users/create", POST),
                new Endpoint("/users/me", GET),
                new Endpoint("/users/me", PUT),
                new Endpoint("/users/me", DELETE),
                new Endpoint("/users/me/avatar", PUT),
                new Endpoint("/users/me/change_password", POST),
                new Endpoint("/users/minified", GET),
                new Endpoint("/users/%s", GET),
                new Endpoint("/websites", GET),
                new Endpoint("/websites/%s", GET),
                new Endpoint("/whitelist/activate_cid", POST)
                );
    }
    public static List<Endpoint> getAuthorizedEndpoints() {
        return Arrays.asList(
                new Endpoint("/users/reset_password", POST),
                new Endpoint("/users/reset_password/finish", PUT)
        );
    }
}
