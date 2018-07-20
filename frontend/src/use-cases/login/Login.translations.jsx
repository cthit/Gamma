import LoginTranslations from "./Login.translations.json";

import LoginFormTranslations from "./views/login-form/LoginForm.view.translations.json";

export default {
  Login: {
    ...LoginTranslations,
    View: {
      ...LoginFormTranslations
    }
  }
};
