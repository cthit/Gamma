import SignInTranslations from "./SignIn.translations.json";

import LoginFormTranslations from "./views/login-form/LoginForm.view.translations.json";

export default {
  SignIn: {
    ...SignInTranslations,
    View: {
      ...LoginFormTranslations
    }
  }
};
