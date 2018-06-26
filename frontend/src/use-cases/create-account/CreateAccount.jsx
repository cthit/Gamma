import React, { Component } from "react";

import InputCid from "./views/input-cid";
import CreationOfAccountFinished from "./views/creation-of-account-finished";
import EmailHasBeenSent from "./views/email-has-been-sent";
import InputDataAndCode from "./views/input-data-and-code";

import GammaStepper from "../../common/elements/gamma-stepper";
import MapPathToStep from "../../common/declaratives/map-path-to-step";
import { Fill, Spacing } from "../../common-ui/layout";

class CreateAccount extends Component {
  render() {
    //Texts
    const { SendCid, GetActivationCode, CreateAccount } = this.props.text;

    const { pathname } = this.props.location;

    return (
      <div>
        <Fill>
          <MapPathToStep
            currentPath={pathname}
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
                    text: SendCid,
                    element: <InputCid />
                  },
                  {
                    text: GetActivationCode,
                    element: <EmailHasBeenSent />
                  },
                  {
                    text: CreateAccount,
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
