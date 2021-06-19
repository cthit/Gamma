import { deleteRequest } from "../utils/api";
import { ADMIN_SUPER_GROUP_TYPES_ENDPOINT } from "../utils/endpoints";

export const deleteSuperGroupType = type =>
    deleteRequest(ADMIN_SUPER_GROUP_TYPES_ENDPOINT + type);
