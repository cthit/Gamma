import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import {
  WEBSITES_LOAD_SUCCESSFULLY,
  WEBSITES_LOAD_FAILED,
  WEBSITES_ADD_SUCCESSFULLY,
  WEBSITES_ADD_FAILED,
  WEBSITES_DELETE_SUCCESSFULLY,
  WEBSITES_DELETE_FAILED,
  WEBSITES_CHANGE_SUCCESSFULLY,
  WEBSITES_CHANGE_FAILED
} from "./Websites.actions";

export function websitesLoad() {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .get("http://localhost:8081/admin/websites", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
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
export function websitesAdd(data) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .post("http://localhost:8081/admin/websites", data, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
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
export function websitesDelete(websiteId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .delete("http://localhost:8081/admin/websites/" + websiteId, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
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
export function websitesChange(data, websiteId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .put("http://localhost:8081/admin/websites/" + websiteId, data, {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
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
