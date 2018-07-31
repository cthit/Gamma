import axios from "axios";

import {
  GROUPS_LOAD_SUCCESSFULLY,
  GROUPS_LOAD_FAILED,
  GROUPS_ADD_SUCCESSFULLY,
  GROUPS_ADD_FAILED,
  GROUPS_DELETE_SUCCESSFULLY,
  GROUPS_DELETE_FAILED,
  GROUPS_CHANGE_SUCCESSFULLY,
  GROUPS_CHANGE_FAILED,
  GROUPS_ADD_USER_SUCCESSFULLY,
  GROUPS_ADD_USER_FAILED,
  GROUPS_DELETE_USER_SUCCESSFULLY,
  GROUPS_DELETE_USER_FAILED,
  GROUPS_CHANGE_POST_SUCCESSFULLY,
  GROUPS_CHANGE_POST_FAILED
} from "./Groups.actions";

import token from "../../common/utils/retrievers/token.retrieve";

export function groupsLoad() {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .get("http://localhost:8081/admin/groups", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(groupsLoadSuccessfully(response.data));
          resolve(response);
        })
        .catch(error => {
          dispatch(groupsLoadFailed(error));
          reject(error);
        });
    });
  };
}

export function groupsAdd(group) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .post("http://localhost:8081/admin/groups/new", group, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(groupsAddSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(groupsAddFailed(error));
          reject(error);
        });
    });
  };
}

export function groupsChange(group, groupId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .put("http://localhost:8081/admin/groups/" + groupId)
        .then(response => {
          dispatch(groupsChangeSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(groupsChangeFailed(error));
          reject(error);
        });
    });
  };
}

export function groupsDelete() {
  return dispatch => {
    return new Promise((resolve, reject) => {});
  };
}

export function groupsAddUser() {
  return dispatch => {
    return new Promise((resolve, reject) => {});
  };
}

export function groupsDeleteUser() {
  return dispatch => {
    return new Promise((resolve, reject) => {});
  };
}

export function groupsChangePost() {
  return dispatch => {
    return new Promise((resolve, reject) => {});
  };
}

function groupsAddSuccessfully() {
  return {
    type: GROUPS_ADD_SUCCESSFULLY,
    error: false
  };
}

function groupsAddFailed(error) {
  return {
    type: GROUPS_ADD_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function groupsLoadSuccessfully(data) {
  return {
    type: GROUPS_LOAD_SUCCESSFULLY,
    error: false,
    payload: {
      data: data
    }
  };
}

function groupsLoadFailed(error) {
  return {
    type: GROUPS_LOAD_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function groupsChangeSuccessfully() {
  return {
    type: GROUPS_CHANGE_SUCCESSFULLY,
    error: false
  };
}

function groupsChangeFailed(error) {
  return {
    type: GROUPS_CHANGE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}
