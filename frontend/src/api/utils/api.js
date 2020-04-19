import axios from "axios";
import _ from "lodash";

const path = "/api";

export function getRequest(endpoint, convert) {
    var headers = {};

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

export function postRequest(endpoint, data) {
    var headers = {};

    return axios.post(removeLastSlash(path + endpoint), data, {
        headers
    });
}

export function deleteRequest(endpoint, data) {
    var headers = {};

    return axios.delete(removeLastSlash(path + endpoint), {
        data: data,
        headers
    });
}

export function putRequest(endpoint, data) {
    var headers = {};

    return axios.put(removeLastSlash(path + endpoint), data, {
        headers
    });
}

function removeLastSlash(path) {
    return _.trimEnd(path, "/");
}
