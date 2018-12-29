import React, { Component } from "react";

import WebsiteForm from "../common-views/website-form";
import { DigitTranslations, DigitLayout } from "@cthit/react-digit-components";
import translations from "./EditWebsiteDetails.screen.translations.json";
import * as PropTypes from "prop-types";

class EditWebsiteDetails extends Component {
    componentDidMount() {
        const { getWebsites, gammaLoadingFinished } = this.props;

        getWebsites().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const { website, websiteId, websitesChange, toastOpen } = this.props;
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Websites.Screen.WebsiteForm"
                render={text => (
                    <DigitLayout.Center>
                        <WebsiteForm
                            initialValues={website}
                            onSubmit={(values, actions) => {
                                websitesChange(
                                    {
                                        name: values.name,
                                        prettyName: values.prettyName
                                    },
                                    websiteId
                                )
                                    .then(response => {
                                        toastOpen({
                                            text: text.SuccessfullyEdit
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
                            titleText={text.EditWebsite}
                            submitText={text.SaveWebsite}
                        />
                    </DigitLayout.Center>
                )}
            />
        );
    }
}

export default EditWebsiteDetails;
