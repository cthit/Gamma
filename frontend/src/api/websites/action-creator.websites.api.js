import { addWebsite } from "./post.websites.api";
import { deleteWebsite } from "./delete.websites.api";
import { getWebsites } from "./get.websites.api";
import { editWebsite } from "./put.websites.api";

import {
    WEBSITES_LOAD_SUCCESSFULLY,
    WEBSITES_LOAD_FAILED,
    WEBSITES_ADD_SUCCESSFULLY,
    WEBSITES_ADD_FAILED,
    WEBSITES_DELETE_SUCCESSFULLY,
    WEBSITES_DELETE_FAILED,
    WEBSITES_CHANGE_SUCCESSFULLY,
    WEBSITES_CHANGE_FAILED
} from "./actions.websites.api";

export function createGetWebsitesAction() {
    return dispatch => {
        return new Promise((resolve, reject) => {
            getWebsites()
                .then(response => {
                    dispatch(websitesLoadSuccessfully(response.data));
                    resolve(response.data);
                })
                .catch(error => {
                    dispatch(websitesLoadFailed(error));
                    reject(error);
                });
        });
    };
}
export function createAddWebsiteAction(data) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            addWebsite(data)
                .then(response => {
                    dispatch(websitesAddSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(websitesAddFailed(error));
                    reject(error);
                });
        });
    };
}
export function createDeleteWebsiteAction(websiteId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            deleteWebsite(websiteId)
                .then(response => {
                    dispatch(websitesDeleteSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(websitesDeleteFailed(error));
                    reject(error);
                });
        });
    };
}
export function createEditWebsiteAction(data, websiteId) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            editWebsite(websiteId, data)
                .then(response => {
                    dispatch(websitesChangeSuccessfully());
                    resolve(response);
                })
                .catch(error => {
                    dispatch(websitesChangeFailed(error));
                    reject(error);
                });
        });
    };
}

function websitesLoadSuccessfully(websites) {
    return {
        type: WEBSITES_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            websites: [...websites]
        }
    };
}

function websitesLoadFailed(error) {
    return {
        type: WEBSITES_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function websitesAddSuccessfully() {
    return {
        type: WEBSITES_ADD_SUCCESSFULLY,
        error: false
    };
}

function websitesAddFailed(error) {
    return {
        type: WEBSITES_ADD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function websitesDeleteSuccessfully() {
    return {
        type: WEBSITES_DELETE_SUCCESSFULLY,
        error: false
    };
}

function websitesDeleteFailed(error) {
    return {
        type: WEBSITES_DELETE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function websitesChangeSuccessfully() {
    return {
        type: WEBSITES_CHANGE_SUCCESSFULLY,
        error: false
    };
}

function websitesChangeFailed(error) {
    return {
        type: WEBSITES_CHANGE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
