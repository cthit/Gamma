import { getRequest } from "../utils/api";
import { ADMIN_INFO_API_SETTINGS_ENDPOINT } from "../utils/endpoints";

export const getInfoApiSuperGroupTypes = () =>
    getRequest(ADMIN_INFO_API_SETTINGS_ENDPOINT + "/super-group-types");
