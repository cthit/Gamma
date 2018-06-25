import { combineReducers } from "redux";
import { createAccount } from "../use-cases/create-account/CreateAccount.reducer";
import { redirect } from "./views/gamma-redirect/GammaRedirect.reducer";
import { toast } from "./views/gamma-toast/GammaToast.reducer";

export const rootReducer = combineReducers({
  createAccount,
  redirect,
  toast
});
