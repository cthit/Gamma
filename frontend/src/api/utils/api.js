import axios from "axios";
import _ from "lodash";

const path = process.env.REACT_APP_BACKEND_URL || "http://localhost:8081/api";

export function getRequest(endpoint, includeAuthorization = true, convert) {
    var headers = {};

    if (includeAuthorization) {
        headers = {
            Authorization: "Bearer " + token()
        };
    }

    if (convert != null) {
        return new Promise((resolve, reject) => {
            axios
                .get(removeLastSlash(path + endpoint), {
                    headers
                })
                .then(response => resolve(convert(response)))
                .catch(error => reject(error));
        });
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

export function deleteRequest(endpoint, data, includeAuthorization = true) {
    var headers = {};

    if (includeAuthorization) {
        headers = {
            Authorization: "Bearer " + token()
        };
    }

    return axios.delete(removeLastSlash(path + endpoint), {
        data: data,
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
