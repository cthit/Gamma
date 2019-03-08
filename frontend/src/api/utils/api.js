import axios from "axios";
import _ from "lodash";

const path = (process.env.REACT_APP_BACKEND_URL || "http://localhost:8081/") + "api";

export function getRequest(endpoint) {
    return axios.get(removeLastSlash(path + endpoint), {
        headers: {
            Authorization: "Bearer " + token()
        }
    });
}

export function postRequest(endpoint, data) {
    return axios.post(removeLastSlash(path + endpoint), data, {
        headers: {
            Authorization: "Bearer " + token()
        }
    });
}

export function deleteRequest(endpoint) {
    return axios.delete(removeLastSlash(path + endpoint), {
        headers: {
            Authorization: "Bearer " + token()
        }
    });
}

export function putRequest(endpoint, data) {
    return axios.put(removeLastSlash(path + endpoint), data, {
        headers: {
            Authorization: "Bearer " + token()
        }
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
