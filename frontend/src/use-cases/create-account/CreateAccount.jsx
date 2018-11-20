import React, { Component } from "react";
import PropTypes from "prop-types";

import InputCid from "./views/input-cid";
import CreationOfAccountFinished from "./views/creation-of-account-finished";
import EmailHasBeenSent from "./views/email-has-been-sent";
import InputDataAndCode from "./views/input-data-and-code";

import GammaStepper from "../../common/elements/gamma-stepper";
import MapPathToStep from "../../common/declaratives/map-path-to-step";
import { Fill, Spacing } from "../../common-ui/layout";

import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./CreateAccount.translations.json";

class CreateAccount extends React.Component {
    constructor(props) {
        super();

        props.gammaLoadingFinished();
    }

    render() {
        const { location } = this.props;

        console.log(translations);

        return (
            <DigitTranslations
                translations={translations}
                uniquePath="CreateAccount"
                render={text => (
                    <Fill>
                        {console.log(text)}
                        <MapPathToStep
                            currentPath={location.pathname}
                            pathToStepMap={{
                                "/create-account": 0,
                                "/create-account/email-sent": 1,
                                "/create-account/input": 2,
                                "/create-account/finished": 3
                            }}
                            render={step => (
                                <GammaStepper
                                    activeStep={step}
                                    steps={[
                                        {
                                            text: text.SendCid,
                                            element: <InputCid />
                                        },
                                        {
                                            text: text.GetActivationCode,
                                            element: <EmailHasBeenSent />
                                        },
                                        {
                                            text: text.CreateAccount,
                                            element: <InputDataAndCode />
                                        }
                                    ]}
                                    finishedElement={
                                        <CreationOfAccountFinished />
                                    }
                                />
                            )}
                        />
                    </Fill>
                )}
            />
        );
    }
}

CreateAccount.propTypes = {
    location: PropTypes.object.isRequired
};

export default CreateAccount;
