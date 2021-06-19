import { postRequest } from "../utils/api";

export const addAuthorityLevel = data =>
    postRequest("/admin/authority/level", data);

export const addPostToAuthorityLevel = data =>
    postRequest("/admin/authority/post", data);

export const addSuperGroupToAuthorityLevel = data =>
    postRequest("/admin/authority/supergroup", data);

export const addUserToAuthorityLevel = data =>
    postRequest("/admin/authority/user", data);
