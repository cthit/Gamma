import { combineReducers } from "redux";
import { localizeReducer as localize } from "react-localize-redux";

import { redirect } from "./views/gamma-redirect/GammaRedirect.view.reducer";
import { toast } from "./views/gamma-toast/GammaToast.view.reducer";
import { user } from "./elements/user-information/UserInformation.element.reducer";

import { users } from "../use-cases/users/Users.reducer";
import { createAccount } from "../use-cases/create-account/CreateAccount.reducer";
import { posts } from "../use-cases/posts/Posts.reducer";
import { whitelist } from "../use-cases/whitelist/Whitelist.reducer";
import { groups } from "../use-cases/groups/Groups.reducer";

export const rootReducer = combineReducers({
  createAccount,
  redirect,
  toast,
  localize,
  user,
  users,
  posts,
  whitelist,
  groups
});
