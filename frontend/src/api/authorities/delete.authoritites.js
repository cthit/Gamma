import { deleteRequest } from "../utils/api";

export const deleteAuthorityLevel = id =>
    deleteRequest("/admin/authority/level/" + id);

export const deleteAuthority = id => deleteRequest("/admin/authority/" + id);
