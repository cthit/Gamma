import axios from "axios";

import {
  SIGN_IN_VALIDATE,
  SIGN_IN_VALIDATING,
  SIGN_IN_VALIDATE_FAILED,
  SIGN_IN_VALIDATE_SUCCESSFULLY
} from "./SignIn.actions";

import { toastOpen } from "../../app/views/gamma-toast/GammaToast.view.action-creator";

export function signIn(
  data,
  persistant,
  successMsg,
  errorMsg,
  networkErrorMsg
) {
  return dispatch => {
    dispatch(signInValidating());
    axios
      .post("http://localhost:8081/users/login", data, {
        "Content-Type": "application/json"
      })
      .then(response => {
        const token = response.data;
        if (persistant) {
          localStorage.token = token;
        } else {
          sessionStorage.token = token;
        }
        dispatch(signInValidateSuccessfully(successMsg));
        dispatch(
          toastOpen({
            text: successMsg,
            duration: 3000
          })
        );
      })
      .catch(error => {
        const errorStatus = error.response.data;
        var e = "";
        console.log(errorStatus);
        switch (errorStatus) {
          case "INCORRECT_CID_OR_PASSWORD":
            e = errorMsg;
            break;
          default:
            e = networkErrorMsg;
            break;
        }
        dispatch(signInValidateSuccessfully(e));
        dispatch(
          toastOpen({
            text: e,
            duration: 10000
          })
        );
      });
  };
}

export function signInValidating() {
  return {
    type: SIGN_IN_VALIDATING,
    error: false
  };
}

export function signInValidateFailed(error) {
  return {
    type: SIGN_IN_VALIDATE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

export function signInValidateSuccessfully() {
  return {
    type: SIGN_IN_VALIDATE_SUCCESSFULLY,
    error: false
  };
}
