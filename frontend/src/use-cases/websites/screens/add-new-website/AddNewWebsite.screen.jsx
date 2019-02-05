import React, { Component } from "react";
import WebsiteForm from "../common-views/website-form";
import { DigitTranslations, DigitLayout } from "@cthit/react-digit-components";
import translations from "./AddNewWebsite.screen.translations.json";
import { NAME, PRETTY_NAME } from "../../../../api/websites/props.websites.api";

function createInitialValues() {
    const output = {};

    output[NAME] = "";
    output[PRETTY_NAME] = "";

    return output;
}

class AddNewWebsite extends Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        const { websitesAdd, toastOpen } = this.props;
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Websites.Screen.AddNewWebsite"
                render={text => (
                    <DigitLayout.Center>
                        <WebsiteForm
                            initialValues={createInitialValues()}
                            onSubmit={(values, actions) => {
                                websitesAdd(values)
                                    .then(() => {
                                        toastOpen({
                                            text: text.SuccessfullyAdd
                                        });
                                        actions.resetForm();
                                    })
                                    .catch(() => {
                                        toastOpen({
                                            text: text.SomethingWentWrong,
                                            duration: 6000
                                        });
                                    });
                            }}
                            titleText={text.CreateNewWebsite}
                            submitText={text.SaveWebsite}
                            backButtonTo={"/websites"}
                        />
                    </DigitLayout.Center>
                )}
            />
        );
    }
}

export default AddNewWebsite;
