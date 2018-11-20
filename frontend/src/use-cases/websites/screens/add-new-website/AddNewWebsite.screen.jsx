import React from "react";
import WebsiteForm from "../common-views/website-form";
import { Center } from "../../../../common-ui/layout";
import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./AddNewWebsite.screen.translations.json";

const AddNewWebsite = ({ websitesAdd }) => (
    <DigitTranslations
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
