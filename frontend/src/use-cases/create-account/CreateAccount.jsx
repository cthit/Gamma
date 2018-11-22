import {
  DigitComponentSelector,
  DigitLayout,
  DigitStepper,
  DigitTranslations
} from "@cthit/react-digit-components";
import PropTypes from "prop-types";
import React from "react";
import MapPathToStep from "../../common/declaratives/map-path-to-step";
import translations from "./CreateAccount.translations.json";
import CreationOfAccountFinished from "./views/creation-of-account-finished";
import EmailHasBeenSent from "./views/email-has-been-sent";
import InputCid from "./views/input-cid";
import InputDataAndCode from "./views/input-data-and-code";

class CreateAccount extends React.Component {
  constructor(props) {
    super();

    props.gammaLoadingFinished();
  }

  render() {
    const { location } = this.props;

    return (
      <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
          <DigitLayout.Fill>
            <MapPathToStep
              currentPath={location.pathname}
              pathToStepMap={{
                "/create-account": 0,
                "/create-account/email-sent": 1,
                "/create-account/input": 2,
                "/create-account/finished": 3
              }}
              render={step => (
                <DigitLayout.Column>
                  <DigitStepper
                    activeStep={step}
                    steps={[
                      { text: text.SendCid },
                      { text: text.GetActivationCode },
                      { text: text.CreateAccount }
                    ]}
                  />
                  <DigitComponentSelector
                    activeComponent={step}
                    components={[
                      InputCid,
                      EmailHasBeenSent,
                      InputDataAndCode,
                      CreationOfAccountFinished
                    ]}
                  />
                </DigitLayout.Column>
              )}
            />
          </DigitLayout.Fill>
        )}
      />
    );
  }
}

CreateAccount.propTypes = {
  location: PropTypes.object.isRequired
};

export default CreateAccount;
