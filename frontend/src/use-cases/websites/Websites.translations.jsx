import WebsitesTranslations from "./Websites.translations.json";

import AddNewWebsiteTranslations from "./screens/add-new-website/AddNewWebsite.screen.translations.json";
import EditWebsiteDetailsTranslations from "./screens/edit-website-details/EditWebsiteDetails.screen.translations.json";
import ShowAllWebsitesTranslations from "./screens/show-all-websites/ShowAllWebsites.screen.translations.json";
import ShowWebsiteDetailTranslations from "./screens/show-website-details/ShowWebsiteDetails.screen.translations.json";

export default {
  Websites: {
    ...WebsitesTranslations,
    Screen: {
      ...AddNewWebsiteTranslations,
      ...EditWebsiteDetailsTranslations,
      ...ShowAllWebsitesTranslations,
      ...ShowWebsiteDetailTranslations
    }
  }
};
