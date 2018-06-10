export const CREATE_ACCOUNT_RESET = "create_account_reset";
export const CREATE_ACCOUNT_VALIDATE_CID = "create_account_validate_cid";
export const CREATE_ACCOUNT_VALIDATING_CID = "create_account_validating_cid";
export const CREATE_ACCOUNT_VALIDATE_CID_FAILED =
  "create_account_validate_cid_failed";
export const CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY =
  "create_account_validate_cid_successfully";
export const CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA =
  "create_account_validate_code_and_data";
export const CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA =
  "create_account_validating_code_and_data";
export const CREATE_ACCOUNT_VALIDATE_CODE_FAILED =
  "create_account_validate_code_failed";
export const CREATE_ACCOUNT_VALIDATE_DATA_FAILED =
  "create_account_validate_data_failed";
export const CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY =
  "create_account_validate_code_and_data_successfully";
export const CREATE_ACCOUNT_COMPLETED = "create_account_completed";

export function createAccountReset() {
  return {
    type: CREATE_ACCOUNT_RESET,
    error: false
  };
}

export function createAccountValidateCid(cid) {
  return {
    type: CREATE_ACCOUNT_VALIDATE_CID,
    error: false,
    payload: {
      cid: cid
    }
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

export function createAccountValidateCodeAndData(code, data) {
  return {
    type: CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA,
    error: false,
    payload: {
      code: code,
      data: data
    }
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
