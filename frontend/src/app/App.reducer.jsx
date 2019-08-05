import { activationCodes } from "../use-cases/activation-codes/ActivationCodes.reducer";
import { createAccount } from "../use-cases/create-account/CreateAccount.reducer";
import { groups } from "../use-cases/groups/Groups.reducer";
import { users } from "../use-cases/users/Users.reducer";
import { websites } from "../use-cases/websites/Websites.reducer";
import { user } from "./elements/user-information/UserInformation.element.reducer";
import { loading } from "./views/gamma-loading/GammaLoading.view.reducer";
import { clients } from "../use-cases/clients/Clients.reducer";
import { gammaIntegration } from "./views/gamma-integration/GammaIntegration.view.reducer";
import { superGroups } from "../use-cases/super-groups/SuperGroups.reducer";
import { gdpr } from "../use-cases/gdpr/Gdpr.reducer";

export const rootReducer = {
    createAccount,
    user,
    users,
    groups,
    websites,
    activationCodes,
    loading,
    clients,
    gammaIntegration,
    superGroups,
    gdpr
};
