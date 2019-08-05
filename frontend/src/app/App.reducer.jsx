import { createAccount } from "../use-cases/create-account/CreateAccount.reducer";
import { groups } from "../use-cases/groups/Groups.reducer";
import { users } from "../use-cases/users/Users.reducer";
import { user } from "./elements/user-information/UserInformation.element.reducer";
import { loading } from "./views/gamma-loading/GammaLoading.view.reducer";
import { gammaIntegration } from "./views/gamma-integration/GammaIntegration.view.reducer";
import { superGroups } from "../use-cases/super-groups/SuperGroups.reducer";
import { gdpr } from "../use-cases/gdpr/Gdpr.reducer";

export const rootReducer = {
    createAccount,
    user,
    users,
    groups,
    loading,
    gammaIntegration,
    superGroups,
    gdpr
};
