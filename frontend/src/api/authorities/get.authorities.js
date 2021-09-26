import { getRequest } from "../utils/api";

export const getAuthorityLevel = id =>
    getRequest("/admin/authority/level/" + id);

export const getAuthorityLevels = () => getRequest("/admin/authority/level");
