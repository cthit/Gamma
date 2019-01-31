import React, { Component } from "react";

import WebsiteForm from "../common-views/website-form";
import {
    DigitTranslations,
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import translations from "./EditWebsiteDetails.screen.translations.json";

class EditWebsiteDetails extends Component {
    componentDidMount() {
        const { getWebsite, websiteId, gammaLoadingFinished } = this.props;

        getWebsite(websiteId).then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const { website, websiteId, websitesChange, toastOpen } = this.props;

        return (
            <DigitIfElseRendering
                test={website != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Center>
                                <WebsiteForm
                                    initialValues={website}
                                    onSubmit={(values, actions) => {
                                        websitesChange(values, websiteId)
                                            .then(() => {
                                                toastOpen({
                                                    text: text.SuccessfullyEdit
                                                });
                                                actions.setSubmitting(false);
                                            })
                                            .catch(() => {
                                                toastOpen({
                                                    text:
                                                        text.SomethingWentWrong,
                                                    duration: 6000
                                                });
                                            });
                                    }}
                                    titleText={text.EditWebsite}
                                    submitText={text.SaveWebsite}
                                    backButtonTo={"/websites/" + websiteId}
                                />
                            </DigitLayout.Center>
                        )}
                    />
                )}
            />
        );
    }
}

export default EditWebsiteDetails;
