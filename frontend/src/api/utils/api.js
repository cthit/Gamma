import axios from "axios";
import _ from "lodash";

import { on401 } from "common/utils/error-handling/error-handling";

const path = "/api/internal";

axios.defaults.xsrfCookieName = "XSRF-TOKEN";
axios.defaults.xsrfHeaderName = "X-XSRF-TOKEN";

const error401Redirect = error => {
    try {
        if (
            error != null &&
            error.response !== null &&
            error.response.status === 401
        ) {
            on401();
        }
    } catch (error2) {
        console.log("Error in on401() function");
        console.log(error, error2);
    }
};

export function getRequest(endpoint, convert, redirect = true) {
    if (convert != null) {
        return new Promise((resolve, reject) => {
            axios
                .get(removeLastSlash(path + endpoint))
                .then(response => resolve(convert(response)))
                .catch(error => {
                    if (redirect) {
                        error401Redirect(error);
                    }
                    reject(error);
                });
        });
    }

    return wrapWithPromise(
        () => axios.get(removeLastSlash(path + endpoint)),
        redirect ? error401Redirect : () => {}
    );
}

export function postRequest(endpoint, data, redirect = true) {
    return wrapWithPromise(
        () => axios.post(removeLastSlash(path + endpoint), data),
        redirect ? error401Redirect : () => {}
    );
}

export function deleteRequest(endpoint, data, redirect) {
    return wrapWithPromise(
        () =>
            axios.delete(removeLastSlash(path + endpoint), {
                data
            }),
        redirect ? error401Redirect : () => {}
    );
}

export function putRequest(endpoint, data, redirect) {
    return wrapWithPromise(
        () => axios.put(removeLastSlash(path + endpoint), data),
        redirect ? error401Redirect : () => {}
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
