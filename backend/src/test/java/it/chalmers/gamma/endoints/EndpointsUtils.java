package it.chalmers.gamma.endoints;

import static it.chalmers.gamma.endoints.Method.DELETE;
import static it.chalmers.gamma.endoints.Method.GET;
import static it.chalmers.gamma.endoints.Method.POST;
import static it.chalmers.gamma.endoints.Method.PUT;

import it.chalmers.gamma.domain.dto.access.ApiKeyDTO;
import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import java.util.Arrays;
import java.util.List;

public final class EndpointsUtils { // Doesn't return me endpoints.

    private EndpointsUtils() {

    }

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
                new Endpoint("/superGroups", GET),
                new Endpoint("/superGroups/%s", GET, FKITSuperGroupDTO.class),
                new Endpoint("/superGroups/%s/active", GET, FKITSuperGroupDTO.class),
                new Endpoint("/superGroups/%s/subgroups", GET, FKITSuperGroupDTO.class),
                new Endpoint("/users/minified", GET),
                new Endpoint("/users/%s", GET, ITUserDTO.class)
                );
    }
    // TODO Members, to do that, we need to rework Endpoint class a bit. Also,
    //  GOLDAPPS Should be re-added once it has been rewritten
    public static List<Endpoint> getAuthorizedEndpoints() {
        return Arrays.asList(
                new Endpoint("/admin/activation_codes", GET),
                new Endpoint("/admin/activation_codes/%s", GET, ActivationCodeDTO.class),
                new Endpoint("/admin/activation_codes/%s", DELETE, ActivationCodeDTO.class),
                new Endpoint("/admin/api_keys", GET),
                new Endpoint("/admin/api_keys", POST),
                new Endpoint("/admin/api_keys/%s", GET, ApiKeyDTO.class),
                new Endpoint("/admin/api_keys/%s", DELETE, ApiKeyDTO.class),
                new Endpoint("/admin/authority", GET),
                new Endpoint("/admin/authority", GET),
                new Endpoint("/admin/authority/level", GET),
                new Endpoint("/admin/authority/level", POST),
                new Endpoint("/admin/authority/level/%s", DELETE, AuthorityLevelDTO.class),
                new Endpoint("/admin/authority/%s", GET, AuthorityDTO.class),
                new Endpoint("/admin/authority/%s", DELETE, AuthorityDTO.class),
                new Endpoint("/admin/clients", GET),
                new Endpoint("/admin/clients", POST),
                new Endpoint("/admin/clients/%s", GET, ITClientDTO.class),
                new Endpoint("/admin/clients/%s", PUT, ITClientDTO.class),
                new Endpoint("/admin/clients/%s", DELETE, ITClientDTO.class),
                new Endpoint("/admin/gdpr/minified", GET),
                new Endpoint("/admin/gdpr/%s", PUT, ITUserDTO.class),
                new Endpoint("/admin/groups", POST),
                new Endpoint("/admin/groups/%s", PUT, FKITGroupDTO.class),
                new Endpoint("/admin/groups/%s", DELETE, FKITGroupDTO.class),
                new Endpoint("/admin/groups/%s/avatar", PUT, FKITGroupDTO.class),
                new Endpoint("/admin/groups/posts", POST),
                new Endpoint("/admin/groups/posts/%s", PUT, PostDTO.class),
                new Endpoint("/admin/groups/posts/%s", DELETE, PostDTO.class),
                new Endpoint("/admin/groups/posts/%s/usage", GET, PostDTO.class),
                new Endpoint("/admin/superGroups", POST),
                new Endpoint("/admin/superGroups/%s", PUT, FKITSuperGroupDTO.class),
                new Endpoint("/admin/superGroups/%s", DELETE, FKITSuperGroupDTO.class),
                new Endpoint("/admin/users", GET),
                new Endpoint("/admin/users", POST),
                new Endpoint("/admin/users/%s", GET, ITUserDTO.class),
                new Endpoint("/admin/users/%s", PUT, ITUserDTO.class),
                new Endpoint("/admin/users/%s", DELETE, ITUserDTO.class),
                new Endpoint("/admin/users/%s/change_password", PUT, ITUserDTO.class),
                new Endpoint("/admin/users/whitelist", GET),
                new Endpoint("/admin/users/whitelist", POST),
                new Endpoint("/admin/users/whitelist/%s/valid", GET, WhitelistDTO.class),
                new Endpoint("/admin/users/whitelist/%s", GET, WhitelistDTO.class),
                new Endpoint("/admin/users/whitelist/%s", PUT, WhitelistDTO.class),
                new Endpoint("/admin/users/whitelist/%s", DELETE, WhitelistDTO.class),
                new Endpoint("/admin/websites", POST),
                new Endpoint("/admin/websites/%s", PUT, WebsiteDTO.class),
                new Endpoint("/admin/websites/%s", DELETE, WebsiteDTO.class)


                );
    }

    public static List<Endpoint> getMeEndpoints() {
        return Arrays.asList(
                new Endpoint("/users/me", GET),
                new Endpoint("/users/me", PUT),
                new Endpoint("/users/me", DELETE),
                new Endpoint("/users/me/avatar", PUT),
                new Endpoint("/users/me/change_password", POST));
    }

    public static List<Endpoint> getNonAuthorizedEndpoints() {
        return Arrays.asList(
                new Endpoint("/users/reset_password", POST),
                new Endpoint("/users/reset_password/finish", PUT),
                new Endpoint("/login", GET),
                new Endpoint("/whitelist/activate_cid", POST),
                new Endpoint("/users/create", POST)
                );
    }
}
