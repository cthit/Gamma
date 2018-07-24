import UsersTranslations from "./Users.translations.json";

import ShowAllUsersTranslations from "./screens/show-all-users/ShowAllUsers.screen.translations.json";
import ShowUserDetailsTranslations from "./screens/show-user-details/ShowUserDetails.screen.translations.json";

export default {
  Users: {
    ...UsersTranslations,
    Screen: {
      ...ShowAllUsersTranslations,
      ...ShowUserDetailsTranslations
    }
  }
};
