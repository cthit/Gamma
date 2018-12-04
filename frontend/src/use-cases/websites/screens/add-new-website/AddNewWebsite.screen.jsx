import React from "react";
import WebsiteForm from "../common-views/website-form";
import { DigitTranslations, DigitLayout } from "@cthit/react-digit-components";
import translations from "./AddNewWebsite.screen.translations.json";

const AddNewWebsite = ({ websitesAdd, toastOpen }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Websites.Screen.AddNewWebsite"
        render={text => (
            <DigitLayout.Center>
                <WebsiteForm
                    initialValues={{ name: "", prettyName: "" }}
                    onSubmit={(values, actions) => {
                        websitesAdd(values)
                            .then(response => {
                                toastOpen({
                                    text: text.SuccessfullyAdd
                                });
                                actions.resetForm();
                            })
                            .catch(error => {
                                toastOpen({
                                    text: text.SomethingWentWrong,
                                    duration: 6000
                                });
                            });
                    }}
                    titleText={text.CreateNewWebsite}
                    submitText={text.SaveWebsite}
                />
            </DigitLayout.Center>
        )}
    />
);

export default AddNewWebsite;
