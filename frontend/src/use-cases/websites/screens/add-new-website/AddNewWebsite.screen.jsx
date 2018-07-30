import React from "react";
import EditWebsite from "../common-views/edit-website";
import { Center } from "../../../../common-ui/layout";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./AddNewWebsite.screen.translations.json";

const AddNewWebsite = ({ websitesAdd }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Websites.Screen.AddNewWebsite"
    render={text => (
      <Center>
        <EditWebsite
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
