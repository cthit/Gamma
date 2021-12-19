import { postRequest } from "../utils/api";
import { ADMIN_INFO_API_SETTINGS_ENDPOINT } from "../utils/endpoints";

export const setInfoApiSuperGroupTypes = request =>
    postRequest(
        ADMIN_INFO_API_SETTINGS_ENDPOINT + "/super-group-types",
        request
    );
