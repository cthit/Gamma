import axios from "axios";

import token from "../../common/utils/retrievers/token.retrieve";

import {
  ACTIVATION_CODES_LOAD_SUCCESSFULLY,
  ACTIVATION_CODES_LOAD_FAILED
} from "./ActivationCodes.actions";

export function activationCodesLoad() {
  console.log("Hej");
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
