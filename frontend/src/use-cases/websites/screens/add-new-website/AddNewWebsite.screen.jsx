import React from "react";
import WebsiteForm from "../common-views/website-form";
import { DigitTranslations, DigitLayout } from "@cthit/react-digit-components";
import translations from "./AddNewWebsite.screen.translations.json";

const AddNewWebsite = ({ websitesAdd }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Websites.Screen.AddNewWebsite"
        render={text => (
            <DigitLayout.Center>
                <WebsiteForm
                    initialValues={{ name: "", prettyName: "" }}
                    onSubmit={(values, actions) => {
                        websitesAdd(values);
                    }}
                    titleText={text.CreateNewWebsite}
                    submitText={text.SaveWebsite}
                />
            </DigitLayout.Center>
        )}
    />
);

export default AddNewWebsite;
