import axios from "axios";
import token from "../../common/utils/retrievers/token.retrieve";
import {
  USERS_CHANGE_FAILED,
  USERS_CHANGE_SUCCESSFULLY,
  USERS_DELETE_FAILED,
  USERS_DELETE_SUCCESSFULLY,
  USERS_LOAD_FAILED,
  USERS_LOAD_SUCCESSFULLY
} from "./Users.actions";

export function usersLoad() {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .get("http://localhost:8081/users/minified", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(usersLoadSuccessfully(response.data));
          resolve(response.data);
        })
        .catch(error => {
          dispatch(usersLoadFailed(error));
          reject(error);
        });
    });
  };
}

export function usersChange(user, cid) {
  console.log("Hej");
  console.log(user);
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .put("http://localhost:8081/admin/users/" + cid, user, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(usersChangeSuccessfully());
          resolve(response.data);
        })
        .catch(error => {
          dispatch(usersChangeFailed(error));
          reject(error);
        });
    });
  };
}

export function usersDelete(cid) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .delete("http://localhost:8081/admin/users/" + cid, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(usersDeleteSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(usersDeleteFailed(error));
          reject(error);
        });
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
  return {
    type: USERS_CHANGE_SUCCESSFULLY,
    error: false
  };
}

function usersChangeFailed(error) {
  return {
    type: USERS_CHANGE_FAILED,
    error: false,
    payload: {
      error: error
    }
  };
}

function usersDeleteSuccessfully() {
  return {
    type: USERS_DELETE_SUCCESSFULLY,
    error: false
  };
}

function usersDeleteFailed(error) {
  return {
    type: USERS_DELETE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}
