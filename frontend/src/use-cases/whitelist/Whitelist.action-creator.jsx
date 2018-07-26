import axios from "axios";

import {
  WHITELIST_LOAD,
  WHITELIST_LOADING,
  WHITELIST_LOAD_SUCCESSFULLY,
  WHITELIST_LOAD_FAILED,
  WHITELIST_ADD,
  WHITELIST_ADD_SUCCESSFULLY,
  WHITELIST_ADD_FAILED,
  WHITELIST_REMOVE,
  WHITELIST_REMOVE_SUCCESSFULLY,
  WHITELIST_REMOVE_FAILED,
  WHITELIST_CHANGE,
  WHITELIST_CHANGE_SUCCESSFULLY,
  WHITELIST_CHANGE_FAILED
} from "./Whitelist.actions";

import token from "../../common/utils/retrievers/token.retrieve";

export function whitelistLoad() {
  return dispatch => {
    dispatch(whitelistLoading());
    return new Promise((resolve, reject) => {
      axios
        .get("http://localhost:8081/admin/users/whitelist", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
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
      axios
        .post("http://localhost:8081/admin/users/whitelist", whitelist, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
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

export function whitelistRemove(whitelistId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .delete("http://localhost:8081/admin/users/whitelist/" + whitelistId, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(whitelistRemoveSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(whitelistRemoveFailed(error));
          reject(error);
        });
    });
  };
}

export function whitelistChange(whitelist, whitelistId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .put(
          "http://localhost:8081/admin/users/whitelist/" + whitelistId,
          whitelist,
          {
            headers: {
              Authorization: "Bearer " + token()
            }
          }
        )
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
    type: WHITELIST_REMOVE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function whitelistRemoveSuccessfully() {
  return {
    type: WHITELIST_REMOVE_SUCCESSFULLY,
    error: false
  };
}

function whitelistRemoveFailed(error) {
  return {
    type: WHITELIST_REMOVE_FAILED,
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
