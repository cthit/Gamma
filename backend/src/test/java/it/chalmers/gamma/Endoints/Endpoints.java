package it.chalmers.gamma.Endoints;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
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
                new Endpoint("/groups/posts/%s", GET, PostDTO.class),
                new Endpoint("/groups/%s", GET, FKITGroupDTO.class),
                new Endpoint("/groups/%s/members", GET, FKITGroupDTO.class),
                new Endpoint("/groups/%s/minified", GET, FKITGroupDTO.class),
                new Endpoint("/login", GET),
                new Endpoint("/superGroups", GET),
                new Endpoint("/superGroups/%s", GET, FKITSuperGroupDTO.class),
                new Endpoint("/superGroups/%s/active", GET, FKITSuperGroupDTO.class),
                new Endpoint("/superGroups/%s/subgroups", GET, FKITSuperGroupDTO.class),
                new Endpoint("/users/create", POST),
                new Endpoint("/users/me", GET),
                new Endpoint("/users/me", PUT),
                new Endpoint("/users/me", DELETE),
                new Endpoint("/users/me/avatar", PUT),
                new Endpoint("/users/me/change_password", POST),
                new Endpoint("/users/minified", GET),
                new Endpoint("/users/%s", GET, ITUser.class),
                new Endpoint("/websites", GET),
                new Endpoint("/websites/%s", GET, WebsiteDTO.class),
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
