import { createAccount } from "../use-cases/create-account/CreateAccount.reducer";
import { user } from "./elements/user-information/UserInformation.element.reducer";
import { loading } from "./views/delta-loading/DeltaLoading.view.reducer";
import { deltaIntegration } from "./views/delta-integration/DeltaIntegration.view.reducer";
import { gdpr } from "../use-cases/gdpr/Gdpr.reducer";

export const rootReducer = {
    createAccount,
    user,
    loading,
    deltaIntegration,
    gdpr
};
