import { getBackendUrl } from "../configs/envVariablesLoader";

export const on401 = () => {
    window.location.href = getBackendUrl() + "/login";
};
