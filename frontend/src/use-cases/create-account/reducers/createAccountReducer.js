import {
  CREATE_ACCOUNT_RESET,
  CREATE_ACCOUNT_VALIDATE_CID,
  CREATE_ACCOUNT_VALIDATING_CID,
  CREATE_ACCOUNT_VALIDATE_CID_FAILED,
  CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY,
  CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA,
  CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA,
  CREATE_ACCOUNT_VALIDATE_CODE_FAILED,
  CREATE_ACCOUNT_VALIDATE_DATA_FAILED,
  CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY,
  CREATE_ACCOUNT_COMPLETED
} from "../actions/createAccountActions";

export function createAccount(state = {}, action) {
  switch (action.type) {
    case CREATE_ACCOUNT_RESET:
      return {
        stage: 1,
        validatingCid: false,
        errorValidatingCid: false,
        validatingCodeAndData: false,
        cid: null,
        error: null
      };
    case CREATE_ACCOUNT_VALIDATE_CID:
      return Object.assign({}, state, {
        cid: action.cid,
        errorValidatingCid: false,
        error: null
      });
    case CREATE_ACCOUNT_VALIDATING_CID:
      return Object.assign({}, state, {
        validatingCid: true
      });
    case CREATE_ACCOUNT_VALIDATE_CID_FAILED:
      return Object.assign({}, state, {
        validatingCid: false,
        errorValidatingCid: true,
        cid: null,
        error: action.payload.error
      });
    case CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY:
      return Object.assign({}, state, {
        stage: 2,
        validatingCid: false
      });
    case CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA:
      return Object.assign({}, state, {
        errorValidatingCode: false,
        errorValidatingData: false,
        error: null
      });
    case CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA:
      return Object.assign({}, state, {
        validatingCodeAndData: true
      });
    case CREATE_ACCOUNT_VALIDATE_CODE_FAILED:
      return Object.assign({}, state, {
        validatingCodeAndData: false,
        errorVlaidatingCode: true,
        errorValidatingData: false,
        error: action.payload.error
      });
    case CREATE_ACCOUNT_VALIDATE_DATA_FAILED:
      return Object.assign({}, state, {
        validatingCodeAndData: false,
        errorVlaidatingCode: false,
        errorValidatingData: true,
        error: action.payload.error
      });
    case CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY:
      return Object.assign({}, state, {
        validatingCodeAndData: false,
        errorVlaidatingCode: false,
        errorValidatingData: false,
        error: null
      });
    case CREATE_ACCOUNT_COMPLETED:
      return Object.assign({}, state, {});
    default:
      return {};
  }
}
