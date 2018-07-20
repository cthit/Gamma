import axios from "axios";

import {
  LOGIN_VALIDATE,
  LOGIN_VALIDATING,
  LOGIN_VALIDATE_FAILED,
  LOGIN_VALIDATE_SUCCESSFULLY
} from "./Login.actions";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { toastOpen } from "../../app/views/gamma-toast/GammaToast.view.action-creator";
import { userUpdateMe } from "../../app/elements/user-information/UserInformation.element.action-creator";

export function login(data, persistant, successMsg, errorMsg, networkErrorMsg) {
  return dispatch => {
    dispatch(loginValidating());
    axios
      .post("http://localhost:8081/users/login", data, {
        "Content-Type": "application/json"
      })
      .then(response => {
        const token = response.data;
        if (persistant) {
          localStorage.token = token;
          delete sessionStorage.token;
        } else {
          sessionStorage.token = token;
          delete localStorage.token;
        }
        dispatch(loginValidateSuccessfully(successMsg));
        dispatch(
          toastOpen({
            text: successMsg,
            duration: 3000
          })
        );
        dispatch(redirectTo("/home"));
        dispatch(userUpdateMe());
      })
      .catch(error => {
        if (error.response == null) {
          console.log(error);
        } else {
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
          dispatch(loginValidateFailed(e));
          dispatch(
            toastOpen({
              text: e,
              duration: 10000
            })
          );
        }
      });
  };
}

export function loginValidating() {
  return {
    type: LOGIN_VALIDATING,
    error: false
  };
}

export function loginValidateFailed(error) {
  return {
    type: LOGIN_VALIDATE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

export function loginValidateSuccessfully() {
  return {
    type: LOGIN_VALIDATE_SUCCESSFULLY,
    error: false
  };
}
