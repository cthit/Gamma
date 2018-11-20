import React from "react";

import WebsiteForm from "../common-views/website-form";
import { Center } from "../../../../common-ui/layout";
import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./EditWebsiteDetails.screen.translations.json";

const EditWebsiteDetails = ({ website, websiteId, websitesChange }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Websites.Screen.WebsiteForm"
        render={text => (
            <Center>
                <WebsiteForm
                    initialValues={website}
                    onSubmit={(values, actions) => {
                        websitesChange(
                            {
                                name: values.name,
                                prettyName: values.prettyName
                            },
                            websiteId
                        ).then(response => {});
                    }}
                    titleText={text.EditWebsite}
                    submitText={text.SaveWebsite}
                />
            </Center>
        )}
    />
);

export default EditWebsiteDetails;
