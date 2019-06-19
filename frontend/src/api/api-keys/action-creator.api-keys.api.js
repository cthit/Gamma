import { getApiKey, getApiKeys } from "./get.api-keys.api";

import {
    API_KEY_ADD_FAILED,
    API_KEY_ADD_LOADING,
    API_KEY_ADD_SUCCESSFULLY,
    API_KEY_DELETE_FAILED,
    API_KEY_DELETE_LOADING,
    API_KEY_DELETE_SUCCESSFULLY,
    API_KEY_GET_FAILED,
    API_KEY_GET_LOADING,
    API_KEY_GET_SUCCESSFULLY,
    API_KEYS_LOAD_FAILED,
    API_KEYS_LOAD_LOADING,
    API_KEYS_LOAD_SUCCESSFULLY
} from "./actions.api-keys.api";
import { requestPromise } from "../utils/requestPromise";
import { deleteApiKey } from "./delete.api-keys.api";
import { addApiKey } from "./post.api-keys.api";

export function createGetApiKeysAction() {
    return requestPromise(
        () => {
            return getApiKeys();
        },
        apiKeysLoadLoading,
        apiKeysLoadSuccessfully,
        apiKeysLoadFailed,
    );
}

export function createGetApiKeyAction(apiKeyId) {
    requestPromise(
        () => {
            return getApiKey(apiKeyId);
        },
        apiKeyGetLoading,
        apiKeyGetSuccessfully,
        apiKeyGetFailed,
    );
}

export function createDeleteApiKeyAction(apiKeyId) {
    return requestPromise(
        () => {
            return deleteApiKey(apiKeyId);
        },
        apiKeyDeleteLoading,
        apiKeyDeleteSuccessfully,
        apiKeyDeleteFailed,
    );
}

export function createAddApiKeyAction(apiKey) {
    return requestPromise(
        () => {
            return addApiKey(apiKey);
        },
        apiKeyAddLoading,
        apiKeyAddSuccessfully,
        apiKeyAddFailed,
    );
}

function apiKeysLoadLoading() {
    return {
        type: API_KEYS_LOAD_LOADING,
        error: false,
    }
}

function apiKeysLoadSuccessfully(response) {
    return {
        type: API_KEYS_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data,
        },
    };
}

function apiKeysLoadFailed(error) {
    return {
        type: API_KEYS_LOAD_FAILED,
        error: true,
        payload: {
            error: error,
        },
    };
}

function apiKeyGetLoading() {
    return {
        type: API_KEY_GET_LOADING,
        error: false,
    };
}

function apiKeyGetSuccessfully(response) {
    return {
        type: API_KEY_GET_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data,
        },
    };
}

function apiKeyGetFailed(error) {
    return {
        type: API_KEY_GET_FAILED,
        error: true,
        payload: {
            error: error,
        },
    };
}

function apiKeyDeleteLoading() {
    return {
        type: API_KEY_DELETE_LOADING,
        error: false,
    };
}

function apiKeyDeleteSuccessfully(response) {
    return {
        type: API_KEY_DELETE_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data,
        },
    };
}

function apiKeyDeleteFailed(error) {
    return {
        type: API_KEY_DELETE_FAILED,
        error: true,
        payload: {
            error: error,
        },
    };
}

function apiKeyAddLoading() {
    return {
        type: API_KEY_ADD_LOADING,
        error: false,
    };
}

function apiKeyAddSuccessfully(response) {
    return {
        type: API_KEY_ADD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data,
        },
    };
}

function apiKeyAddFailed(error) {
    return {
        type: API_KEY_ADD_FAILED,
        error: true,
        payload: {
            error: error,
        },
    };
}