import axios from "axios";
import _ from "lodash";
import { on401 } from "../../common/utils/error-handling/error-handling";

const path = "/api";

const error401Redirect = error => {
    if (error != null && error.response.status === 401) {
        on401();
    }
};

export function getRequest(endpoint, convert) {
    if (convert != null) {
        return new Promise((resolve, reject) => {
            axios
                .get(removeLastSlash(path + endpoint))
                .then(response => resolve(convert(response)))
                .catch(error => {
                    error401Redirect(error);
                    reject(error);
                });
        });
    }

    return wrapWithPromise(
        () => axios.get(removeLastSlash(path + endpoint)),
        error401Redirect
    );
}

export function postRequest(endpoint, data) {
    return wrapWithPromise(
        () => axios.post(removeLastSlash(path + endpoint), data),
        error401Redirect
    );
}

export function deleteRequest(endpoint, data) {
    return wrapWithPromise(
        () =>
            axios.delete(removeLastSlash(path + endpoint), {
                data: data
            }),
        error401Redirect
    );
}

export function putRequest(endpoint, data) {
    return wrapWithPromise(
        () => axios.put(removeLastSlash(path + endpoint), data),
        error401Redirect
    );
}

function wrapWithPromise(promise, c) {
    return new Promise((resolve, reject) => {
        promise()
            .then(response => {
                resolve(response);
            })
            .catch(error => {
                c(error);
                reject(error);
            });
    });
}

function removeLastSlash(path) {
    return _.trimEnd(path, "/");
}
