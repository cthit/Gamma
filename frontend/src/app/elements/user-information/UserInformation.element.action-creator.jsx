import axios from "axios";

import token from "../../../common/utils/retrievers/token.retrieve";

import {
  USER_UPDATE_ME,
  USER_UPDATED_SUCCESSFULLY,
  USER_UPDATED_FAILED,
  USER_NOT_LOGGED_IN,
  USER_LOGOUT,
  USER_LOGOUT_SUCCESSFULLY
} from "./UserInformation.element.actions";

import { toastOpen } from "../../views/gamma-toast/GammaToast.view.action-creator";
import { redirectTo } from "../../views/gamma-redirect/GammaRedirect.view.action-creator";

export function userUpdateMe() {
  if (token() == null) {
    return {
      type: USER_NOT_LOGGED_IN,
      error: false
    };
  } else {
    return dispatch => {
      axios
        .get("http://localhost:8081/users/me", {
          headers: {
            Authorization: "Bearer " + token()
          }
        })
        .then(response => {
          dispatch(userUpdatedSuccessfully(response.data));
        })
        .catch(error => {
          dispatch(userUpdatedFailed(error.response));
          const statusCode =
            error.response == null ? -1 : error.response.data.status;
          switch (statusCode) {
            case 403:
              dispatch(userLogout());
              break;
            default:
              //TODO DISPATCH REDIRECT TO ERROR PAGE
              break;
          }
        });
    };
  }
}

export function userLogout(loggedOutText) {
  return dispatch => {
    delete localStorage.token;
    delete sessionStorage.token;
    dispatch(userLogoutSuccessfully());
    dispatch(redirectTo("/login"));
    if (loggedOutText != null) {
      dispatch(
        toastOpen({
          duration: 3000,
          text: loggedOutText
        })
      );
    }
  };
}

export function userLogoutSuccessfully() {
  return {
    type: USER_LOGOUT_SUCCESSFULLY,
    error: false
  };
}

export function userUpdatedSuccessfully(data) {
  return {
    type: USER_UPDATED_SUCCESSFULLY,
    payload: {
      user: data
    },
    error: false
  };
}

export function userUpdatedFailed(errorData) {
  return {
    type: USER_UPDATED_FAILED,
    error: true,
    payload: {
      error: errorData
    }
  };
}
