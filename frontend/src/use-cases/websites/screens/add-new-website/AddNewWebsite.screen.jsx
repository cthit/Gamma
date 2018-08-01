import React from "react";
import WebsiteForm from "../common-views/website-form";
import { Center } from "../../../../common-ui/layout";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./AddNewWebsite.screen.translations.json";

const AddNewWebsite = ({ websitesAdd }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Websites.Screen.AddNewWebsite"
    render={text => (
      <Center>
        <WebsiteForm
          initialValues={{ name: "", prettyName: "" }}
          onSubmit={(values, actions) => {
            websitesAdd(values);
          }}
          titleText={text.CreateNewWebsite}
          submitText={text.SaveWebsite}
        />
      </Center>
    )}
  />
);

export default AddNewWebsite;
