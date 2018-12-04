import { localizeReducer as localize } from "react-localize-redux";
import { activationCodes } from "../use-cases/activation-codes/ActivationCodes.reducer";
import { createAccount } from "../use-cases/create-account/CreateAccount.reducer";
import { groups } from "../use-cases/groups/Groups.reducer";
import { posts } from "../use-cases/posts/Posts.reducer";
import { users } from "../use-cases/users/Users.reducer";
import { websites } from "../use-cases/websites/Websites.reducer";
import { whitelist } from "../use-cases/whitelist/Whitelist.reducer";
import { user } from "./elements/user-information/UserInformation.element.reducer";
import { loading } from "./views/gamma-loading/GammaLoading.view.reducer";
import { editUsersInGroup } from "../use-cases/groups/screens/edit-users-in-group/EditUsersInGroup.screen.reducer";

export const rootReducer = {
    createAccount,
    localize,
    user,
    users,
    posts,
    whitelist,
    groups,
    websites,
    activationCodes,
    loading,
    editUsersInGroup
};
