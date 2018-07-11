import axios from "axios";

import {
  CREATE_ACCOUNT_VALIDATING_CID,
  CREATE_ACCOUNT_VALIDATE_CID_FAILED,
  CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY,
  CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA,
  CREATE_ACCOUNT_VALIDATE_CODE_FAILED,
  CREATE_ACCOUNT_VALIDATE_DATA_FAILED,
  CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY,
  CREATE_ACCOUNT_COMPLETED
} from "./CreateAccount.actions";

import { redirectTo } from "../../app/views/gamma-redirect/GammaRedirect.view.action-creator";
import { toastOpen } from "../../app/views/gamma-toast/GammaToast.view.action-creator";

export function createAccountValidateCid(data, errorMsg) {
  return dispatch => {
    dispatch(createAccountValidatingCid());
    axios
      .post("http://localhost:8081/whitelist/activate_cid", data, {
        "Content-Type": "application/json"
      })
      .then(response => {
        dispatch(createAccountValidateCidSuccessfully());
        dispatch(redirectTo("/create-account/email-sent"));
      })
      .catch(error => {
        dispatch(createAccountValidateCidFailed(errorMsg));
        dispatch(
          toastOpen({
            text: errorMsg,
            duration: 10000
          })
        );
      });
  };
}

export function createAccountValidatingCid() {
  return {
    type: CREATE_ACCOUNT_VALIDATING_CID,
    error: false
  };
}

export function createAccountValidateCidFailed(error) {
  return {
    type: CREATE_ACCOUNT_VALIDATE_CID_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

export function createAccountValidateCidSuccessfully() {
  return {
    type: CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY,
    error: false
  };
}

//data includes code. Ignore code for now
export function createAccountValidateCodeAndData(data, errorMessages) {
  return dispatch => {
    dispatch(createAccountValidatingCodeAndData());
    axios
      .post("http://localhost:8081/users/create", data, {
        "Content-Type": "application/json"
      })
      .then(response => {
        dispatch(createAccountValidateCodeAndDataSuccessfully());
        dispatch(redirectTo("/create-account/finished"));
      })
      .catch(error => {
        const errorStatus = error["response"].data;
        var errorMessage = "";

        if (!(errorStatus in errorMessages)) {
          errorMessage = errorMessages["SomethingWentWrong"];
        } else {
          errorMessage = errorMessages[errorStatus];
        }

        dispatch(createAccountValidateDataFailed(errorMessage));
        dispatch(
          toastOpen({
            text: errorMessage,
            duration: 5000
          })
        );
      });
  };
}

export function createAccountValidatingCodeAndData() {
  return {
    type: CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA,
    error: false
  };
}

export function createAccountValidateCodeFailed(error) {
  return {
    type: CREATE_ACCOUNT_VALIDATE_CODE_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

export function createAccountValidateDataFailed(error) {
  return {
    type: CREATE_ACCOUNT_VALIDATE_DATA_FAILED,
    error: true,
    payload: {
      error: error
    }
  };
}

export function createAccountValidateCodeAndDataSuccessfully() {
  return {
    type: CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY,
    error: false
  };
}

export function createAccountCompleted() {
  return {
    type: CREATE_ACCOUNT_COMPLETED,
    error: false
  };
}
