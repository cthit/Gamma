import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import { USERS_LOAD_SUCCESSFULLY, USERS_LOAD_FAILED } from "./Users.actions";

export function usersLoad() {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .get("http://localhost:8081/users", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => dispatch(usersLoadSuccessfully(response.data)))
        .catch(error => {
          dispatch(usersLoadFailed(error));
          reject(error);
        });
    });
  };
}

export function usersChange(user, userId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .put("http://localhost:8081/users/" + userId)
        .then(response => {})
        .catch(error => {});
    });
  };
}

function usersLoadFailed(error) {
  return {
    type: USERS_LOAD_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function usersLoadSuccessfully(data) {
  return {
    type: USERS_LOAD_SUCCESSFULLY,
    error: false,
    payload: {
      ...data
    }
  };
}

function usersChangeSuccessfully() {
  return {};
}
