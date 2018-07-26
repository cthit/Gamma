import WhitelistTranslations from "./Whitelist.translations.json";

import AddNewWhitelistItemTranslations from "./screens/add-new-whitelist-item/AddNewWhitelistItem.screen.translations.json";
import ShowWhitelistItemTranslations from "./screens/show-whitelist-item/ShowWhitelistItem.screen.translations.json";

export default {
  Whitelist: {
    ...WhitelistTranslations,
    Screen: {
      ...AddNewWhitelistItemTranslations,
      ...ShowWhitelistItemTranslations
    }
  }
};
