import { getClient, getClients } from "./get.clients.api";

import {
    CLIENT_ADD_FAILED,
    CLIENT_ADD_LOADING,
    CLIENT_ADD_SUCCESSFULLY,
    CLIENT_CHANGE_FAILED,
    CLIENT_CHANGE_LOADING,
    CLIENT_CHANGE_SUCCESSFULLY,
    CLIENT_DELETE_FAILED,
    CLIENT_DELETE_LOADING,
    CLIENT_DELETE_SUCCESSFULLY,
    CLIENT_GET_FAILED,
    CLIENT_GET_LOADING,
    CLIENT_GET_SUCCESSFULLY,
    CLIENTS_LOAD_FAILED,
    CLIENTS_LOAD_LOADING,
    CLIENTS_LOAD_SUCCESSFULLY
} from "./actions.clients.api";
import { requestPromise } from "../utils/requestPromise";
import { editClient } from "./put.clients.api";
import { deleteClient } from "./delete.clients.api";
import { addClient } from "./post.clients.api";

export function createGetClientsAction() {
    return requestPromise(
        () => {
            return getClients();
        },
        clientsLoadLoading,
        clientsLoadSuccessfully,
        clientsLoadFailed
    );
}

export function createGetClientAction(clientId) {
    return requestPromise(
        () => {
            return getClient(clientId);
        },
        clientGetLoading,
        clientGetSuccessfully,
        clientGetFailed
    );
}

export function createChangeClientAction(clientId, client) {
    return requestPromise(
        () => {
            return editClient(clientId, client);
        },
        clientChangeLoading,
        clientChangeSuccessfully,
        clientChangeFailed
    );
}

export function createDeleteClientAction(clientId) {
    return requestPromise(
        () => {
            return deleteClient(clientId);
        },
        clientDeleteLoading,
        clientDeleteSuccessfully,
        clientDeleteFailed
    );
}

export function createAddClientAction(client) {
    return requestPromise(
        () => {
            return addClient(client);
        },
        clientAddLoading,
        clientAddSuccessfully,
        clientAddFailed
    );
}

function clientsLoadLoading() {
    return {
        type: CLIENTS_LOAD_LOADING,
        error: false
    };
}

function clientsLoadSuccessfully(response) {
    return {
        type: CLIENTS_LOAD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function clientsLoadFailed(error) {
    return {
        type: CLIENTS_LOAD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function clientGetLoading() {
    return {
        type: CLIENT_GET_LOADING,
        error: false
    };
}

function clientGetSuccessfully(response) {
    return {
        type: CLIENT_GET_SUCCESSFULLY,
        error: false
    };
}

function clientGetFailed(error) {
    return {
        type: CLIENT_GET_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function clientDeleteLoading() {
    return {
        type: CLIENT_DELETE_LOADING,
        error: false
    };
}

function clientDeleteSuccessfully(response) {
    return {
        type: CLIENT_DELETE_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function clientDeleteFailed(error) {
    return {
        type: CLIENT_DELETE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function clientChangeLoading() {
    return {
        type: CLIENT_CHANGE_LOADING,
        error: false
    };
}

function clientChangeSuccessfully(response) {
    return {
        type: CLIENT_CHANGE_SUCCESSFULLY,
        error: false
    };
}

function clientChangeFailed(error) {
    return {
        type: CLIENT_CHANGE_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}

function clientAddLoading() {
    return {
        type: CLIENT_ADD_LOADING,
        error: false
    };
}

function clientAddSuccessfully(response) {
    return {
        type: CLIENT_ADD_SUCCESSFULLY,
        error: false,
        payload: {
            data: response.data
        }
    };
}

function clientAddFailed(error) {
    return {
        type: CLIENT_ADD_FAILED,
        error: true,
        payload: {
            error: error
        }
    };
}
