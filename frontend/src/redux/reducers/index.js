import { combineReducers } from "redux";
import { createAccount } from "../../use-cases/create-account/reducers/createAccountReducer";
import { redirect } from "../../redux/reducers/redirectReducer";

export const rootReducer = combineReducers({
  createAccount,
  redirect
});
