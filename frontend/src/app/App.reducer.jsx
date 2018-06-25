import { combineReducers } from "redux";
import { createAccount } from "../use-cases/create-account/CreateAccount.reducer";
import { redirect } from "./views/gamma-redirect/GammaRedirect.view.reducer";
import { toast } from "./views/gamma-toast/GammaToast.view.reducer";
import { localizeReducer as localize } from "react-localize-redux";

export const rootReducer = combineReducers({
  createAccount,
  redirect,
  toast,
  localize
});
