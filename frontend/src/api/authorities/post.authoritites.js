import { postRequest } from "../utils/api";

export const addAuthorityLevel = data =>
    postRequest("/admin/authority/level", data);

export const addToAuthorityLevel = data =>
    postRequest("/admin/authority", data);
