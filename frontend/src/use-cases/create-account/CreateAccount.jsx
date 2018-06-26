import React, { Component } from "react";

import InputDataAndCode from "./views/input-data-and-code";
import InputCid from "./views/input-cid";
import CreationOfAccountFinished from "./views/creation-of-account-finished";
import EmailHasBeenSent from "./views/email-has-been-sent";

import { Fill, Spacing } from "../../common-ui/layout";
import GammaStepper from "../../common/views/gamma-stepper";
import MapPathToStep from "../../common/declaratives/map-path-to-step";

class CreateAccount extends Component {
  render() {
    return (
      <div>
        <Fill>
          <MapPathToStep
            currentPath={this.props.location.pathname}
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
                    text: this.props.text.SendCid,
                    element: <InputCid />
                  },
                  {
                    text: this.props.text.GetActivationCode,
                    element: <EmailHasBeenSent />
                  },
                  {
                    text: this.props.text.CreateAccount,
                    element: <InputDataAndCode />
                  }
                ]}
                finishedElement={<CreationOfAccountFinished />}
              />
            )}
          />
        </Fill>
        <Spacing />
      </div>
    );
  }
}

export default CreateAccount;
