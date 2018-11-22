import axios from "axios";

const path = "http://localhost:8081";

export function getRequest(endpoint) {
    return axios.get(path + endpoint, {
        headers: {
            Authorization: "Bearer " + token()
        }
    });
}

export function postRequest(endpoint, data) {
    return axios.post(path + endpoint, data, {
        headers: {
            Authorization: "Bearer " + token()
        }
    });
}

export function deleteRequest(endpoint) {
    return axios.delete(path + endpoint, {
        headers: {
            Authorization: "Bearer " + token()
        }
    });
}

export function putRequest(endpoint, data) {
    return axios.put(path + endpoint, data, {
        headers: {
            Authorization: "Bearer " + token()
        }
    });
}

function token() {
    const sessionToken = sessionStorage.token;
    const localToken = localStorage.token;
    return sessionToken != null ? sessionToken : localToken;
}
