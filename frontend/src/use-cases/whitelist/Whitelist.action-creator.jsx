import { deleteWhitelistItem } from "../../api/whitelist/delete.whitelist.api";
import { getWhitelist } from "../../api/whitelist/get.whitelist.api";
import {
  addUserToWhitelist,
  cidIsWhitelisted
} from "../../api/whitelist/post.whitelist.api";
import { editWhitelistItem } from "../../api/whitelist/put.whitelist.api";
import {
  WHITELIST_ADD_SUCCESSFULLY,
  WHITELIST_CHANGE_FAILED,
  WHITELIST_CHANGE_SUCCESSFULLY,
  WHITELIST_DELETE_FAILED,
  WHITELIST_DELETE_SUCCESSFULLY,
  WHITELIST_LOADING,
  WHITELIST_LOAD_FAILED,
  WHITELIST_LOAD_SUCCESSFULLY,
  WHITELIST_VALIDATE_FAILED,
  WHITELIST_VALIDATE_SUCCESSFULLY
} from "./Whitelist.actions";

export function whitelistLoad() {
  return dispatch => {
    dispatch(whitelistLoading());
    return new Promise((resolve, reject) => {
      getWhitelist()
        .then(response => {
          dispatch(whitelistLoadSuccessfully(response.data));
          resolve(response);
        })
        .catch(error => {
          dispatch(whitelistLoadFailed(error));
          resolve(reject);
        });
    });
  };
}

export function whitelistAdd(whitelist) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      addUserToWhitelist(whitelist)
        .then(response => {
          dispatch(whitelistAddSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(whitelistAddFailed(error));
          reject(error);
        });
    });
  };
}

export function whitelistDelete(whitelistId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      deleteWhitelistItem(whitelistId)
        .then(response => {
          dispatch(whitelistDeleteSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(whitelistDeleteFailed(error));
          reject(error);
        });
    });
  };
}

export function whitelistChange(whitelist, whitelistId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      editWhitelistItem(whitelistId, whitelist)
        .then(response => {
          dispatch(whitelistChangeSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(whitelistChangeFailed());
          reject(error);
        });
    });
  };
}

export function whitelistValidate(cid) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      cidIsWhitelisted({ cid: cid })
        .then(response => {
          dispatch(whitelistValidateSuccessfully(response.data));
          resolve(response.data);
        })
        .catch(error => {
          dispatch(whitelistValidateFailed(error));
          reject(error);
        });
    });
  };
}

function whitelistLoading() {
  return {
    type: WHITELIST_LOADING,
    error: false
  };
}

function whitelistLoadSuccessfully(data) {
  return {
    type: WHITELIST_LOAD_SUCCESSFULLY,
    error: false,
    payload: {
      data: [...data]
    }
  };
}

function whitelistLoadFailed(error) {
  return {
    type: WHITELIST_LOAD_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function whitelistAddSuccessfully() {
  return {
    type: WHITELIST_ADD_SUCCESSFULLY,
    error: false
  };
}

function whitelistAddFailed(error) {
  return {
    type: WHITELIST_DELETE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function whitelistDeleteSuccessfully() {
  return {
    type: WHITELIST_DELETE_SUCCESSFULLY,
    error: false
  };
}

function whitelistDeleteFailed(error) {
  return {
    type: WHITELIST_DELETE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function whitelistChangeSuccessfully() {
  return {
    type: WHITELIST_CHANGE_SUCCESSFULLY,
    error: false
  };
}

function whitelistChangeFailed(error) {
  return {
    type: WHITELIST_CHANGE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function whitelistValidateSuccessfully(valid) {
  return {
    type: WHITELIST_VALIDATE_SUCCESSFULLY,
    error: false,
    payload: {
      valid: valid
    }
  };
}

function whitelistValidateFailed(error) {
  return {
    type: WHITELIST_VALIDATE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}
