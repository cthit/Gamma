import { postRequest } from "../utils/api";

export const addAuthorityLevel = data =>
    postRequest("/admin/authority/level", data);
