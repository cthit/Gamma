import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import {
  ACTIVATION_CODES_LOAD_SUCCESSFULLY,
  ACTIVATION_CODES_LOAD_FAILED,
  ACTIVATION_CODES_DELETE_FAILED,
  ACTIVATION_CODES_DELETE_SUCCESSFULLY
} from "./ActivationCodes.actions";

export function activationCodesLoad() {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .get("http://localhost:8081/admin/activation_codes", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(activationCodesLoadSuccessfully(response.data));
          resolve(response.data);
        })
        .catch(error => {
          dispatch(activationCodesLoadFailed(error));
          reject(error);
        });
    });
  };
}

export function activationCodesDelete(activationCodeId) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .delete("http://localhost:8081/admin/activation_codes", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(activationCodesDeleteSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(activationCodesDeleteFailed());
          reject(error);
        });
    });
  };
}

function activationCodesLoadSuccessfully(data) {
  return {
    type: ACTIVATION_CODES_LOAD_SUCCESSFULLY,
    error: false,
    payload: {
      data: data
    }
  };
}

function activationCodesLoadFailed(error) {
  return {
    type: ACTIVATION_CODES_LOAD_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

function activationCodesDeleteSuccessfully() {
  return {
    type: ACTIVATION_CODES_DELETE_SUCCESSFULLY,
    error: false
  };
}

function activationCodesDeleteFailed(error) {
  return {
    type: ACTIVATION_CODES_DELETE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}
