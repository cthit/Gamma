import axios from "axios";
import _ from "lodash";

const path =
    (process.env.REACT_APP_BACKEND_URL || "http://localhost:8081/api");

export function getRequest(endpoint, includeAuthorization = true) {
    var headers = {};

    if (includeAuthorization) {
        headers = {
            Authorization: "Bearer " + token()
        };
    }

    return axios.get(removeLastSlash(path + endpoint), {
        headers
    });
}

export function postRequest(endpoint, data, includeAuthorization = true) {
    var headers = {};

    if (includeAuthorization) {
        headers = {
            Authorization: "Bearer " + token()
        };
    }

    return axios.post(removeLastSlash(path + endpoint), data, {
        headers
    });
}

export function deleteRequest(endpoint, includeAuthorization = true) {
    var headers = {};

    if (includeAuthorization) {
        headers = {
            Authorization: "Bearer " + token()
        };
    }

    return axios.delete(removeLastSlash(path + endpoint), {
        headers
    });
}

export function putRequest(endpoint, data, includeAuthorization = true) {
    var headers = {};

    if (includeAuthorization) {
        headers = {
            Authorization: "Bearer " + token()
        };
    }

    return axios.put(removeLastSlash(path + endpoint), data, {
        headers
    });
}

function removeLastSlash(path) {
    return _.trimEnd(path, "/");
}

function token() {
    const sessionToken = sessionStorage.token;
    const localToken = localStorage.token;
    return sessionToken != null ? sessionToken : localToken;
}
