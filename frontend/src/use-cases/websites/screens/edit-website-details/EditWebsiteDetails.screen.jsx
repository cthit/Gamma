import React from "react";

import EditWebsite from "../common-views/edit-website";
import { Center } from "../../../../common-ui/layout";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./EditWebsiteDetails.screen.translations.json";

const EditWebsiteDetails = ({ website }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Websites.Screen.EditWebsite"
    render={text => (
      <Center>
        <EditWebsite
          initialValues={website}
          onSubmit={(values, actions) => {
            console.log(values);
          }}
          titleText={text.EditWebsite}
          submitText={text.SaveWebsite}
        />
      </Center>
    )}
  />
);

export default EditWebsiteDetails;
