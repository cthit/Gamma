import {
    CREATE_ACCOUNT_VALIDATING_CID,
    CREATE_ACCOUNT_VALIDATE_CID_FAILED,
    CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY,
    CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA,
    CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA,
    CREATE_ACCOUNT_VALIDATE_CODE_FAILED,
    CREATE_ACCOUNT_VALIDATE_DATA_FAILED,
    CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY,
    CREATE_ACCOUNT_COMPLETED
} from "./CreateAccount.actions";

export function createAccount(state = {}, action) {
    switch (action.type) {
        case CREATE_ACCOUNT_VALIDATING_CID:
            return {
                ...state,
                stage: 1,
                validatingCid: true
            };
        case CREATE_ACCOUNT_VALIDATE_CID_FAILED:
            return {
                ...state,
                validatingCid: false,
                errorValidatingCid: true,
                error: action.payload.error
            };
        case CREATE_ACCOUNT_VALIDATE_CID_SUCCESSFULLY:
            return {
                ...state,
                stage: 2,
                validatingCid: false
            };
        case CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA:
            return {
                ...state,
                errorValidatingCode: false,
                errorValidatingData: false,
                error: null
            };
        case CREATE_ACCOUNT_VALIDATING_CODE_AND_DATA:
            return {
                ...state,
                validatingCodeAndData: true
            };
        case CREATE_ACCOUNT_VALIDATE_CODE_FAILED:
            return {
                ...state,
                validatingCodeAndData: false,
                errorVlaidatingCode: true,
                errorValidatingData: false,
                error: action.payload.error
            };
        case CREATE_ACCOUNT_VALIDATE_DATA_FAILED:
            return {
                ...state,
                validatingCodeAndData: false,
                errorVlaidatingCode: false,
                errorValidatingData: true,
                error: action.payload.error
            };
        case CREATE_ACCOUNT_VALIDATE_CODE_AND_DATA_SUCCESSFULLY:
            return {
                ...state,
                validatingCodeAndData: false,
                errorVlaidatingCode: false,
                errorValidatingData: false,
                error: null
            };
        case CREATE_ACCOUNT_COMPLETED:
            return {};
        default:
            return {};
    }
}
