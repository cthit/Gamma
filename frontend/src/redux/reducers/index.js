import { combineReducers } from "redux";
import { createAccount } from "../../use-cases/create-account/reducers/createAccountReducer";
import { redirect } from "../reducers/redirectReducer";
import { toast } from "../reducers/toastReducer";

export const rootReducer = combineReducers({
  createAccount,
  redirect,
  toast
});
