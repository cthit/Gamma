import axios from "axios";

import {
  GDPR_SET_VALUE_SUCCESSFULLY,
  GDPR_SET_VALUE_FAILED
} from "./Gdpr.actions";

export function gdprSetValue(userId, gdprValue) {
  return dispatch => {
    return new Promise((resolve, reject) => {
      axios
        .put("http://localhost:8081/admin/users/" + userId)
        .then(response => {
          dispatch(gdprSetValueSuccessfully());
          resolve(response);
        })
        .catch(error => {
          dispatch(gdprSetValueFailed(erorr));
          reject(error);
        });
    });
  };
}

function gdprSetValueSuccessfully() {
  return {
    type: GDPR_SET_VALUE_SUCCESSFULLY,
    error: false
  };
}

function gdprSetValueFailed(error) {
  return {
    type: GDPR_SET_VALUE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}
