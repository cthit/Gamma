import CreateAccountTranslations from "./CreateAccount.translations.json";

import CreationOfAccountFinishedTranslations from "./views/creation-of-account-finished/CreationOfAccountFinished.view.translations.json";
import EmailHasBeenSentTranslations from "./views/email-has-been-sent/EmailHasBeenSent.view.translations.json";
import InputCidTranslations from "./views/input-cid/InputCid.view.translations.json";
import InputDataAndCodeTranslations from "./views/input-data-and-code/InputDataAndCode.view.translations.json";

export default {
  CreateAccount: {
    ...CreateAccountTranslations,
    View: {
      ...CreationOfAccountFinishedTranslations,
      ...EmailHasBeenSentTranslations,
      ...InputCidTranslations,
      ...InputDataAndCodeTranslations
    }
  }
};
