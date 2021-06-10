import { ADMIN_SUPER_GROUP_TYPES_ENDPOINT } from "../utils/endpoints";
import { postRequest } from "../utils/api";

export const addSuperGroupType = data =>
    postRequest(ADMIN_SUPER_GROUP_TYPES_ENDPOINT, data);
