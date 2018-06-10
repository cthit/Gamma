import { combineReducers } from "redux";
import { createAccount } from "../../use-cases/create-account/reducers/createAccountReducer";

export const rootReducer = combineReducers({
  createAccount
});
