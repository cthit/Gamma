import { getRequest } from "../utils/api";

export const getAuthority = id => getRequest("/admin/authority/" + id);

export const getAuthorities = () => getRequest("/admin/authority");

export const getAuthorityLevels = () => getRequest("/admin/authority/level");
