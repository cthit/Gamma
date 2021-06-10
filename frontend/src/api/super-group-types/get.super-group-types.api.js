import { ADMIN_SUPER_GROUP_TYPES_ENDPOINT } from "../utils/endpoints";
import { getRequest } from "../utils/api";

// DigitCrud likes
export const getSuperGroupTypes = () =>
    getRequest(ADMIN_SUPER_GROUP_TYPES_ENDPOINT, response =>
        response.data.map(type => ({ type }))
    );

export const getSuperGroupTypeUsage = superGroupType =>
    getRequest(
        ADMIN_SUPER_GROUP_TYPES_ENDPOINT + superGroupType + "/usage",
        response => ({ data: { type: superGroupType, usages: response.data } })
    );
