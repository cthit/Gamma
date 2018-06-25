import React, { Component } from "react";

import InputDataAndCode from "./views/input-data-and-code";
import InputCid from "./views/input-cid";
import CreationOfAccountFinished from "./views/creation-of-account-finished";
import EmailHasBeenSent from "./views/email-has-been-sent";

import { Fill, Spacing } from "../../common-ui/layout";
import GammaStepper from "../../common/views/gamma-stepper";

class CreateAccount extends Component {
  constructor() {
    super();

    this.gammaStepperRef = React.createRef();

    //An array could be used, but this feels clearer
    this.stepToPathMap = {
      0: "/create-account",
      1: "/create-account/email-sent",
      2: "/create-account/input",
      3: "/create-account/finished"
    };
  }

  componentDidMount() {
    this.gammaStepperRef.current.setStep(
      this._getStep(this.props.location.pathname)
    );
  }

  componentDidUpdate() {
    this.gammaStepperRef.current.setStep(
      this._getStep(this.props.location.pathname)
    );
  }

  _getStep = path => {
    for (var step in this.stepToPathMap) {
      if (path === this.stepToPathMap[step]) {
        return parseInt(step, 10);
      }
    }
    return -1;
  };

  render() {
    return (
      <div>
        <Fill>
          <GammaStepper
            ref={this.gammaStepperRef}
            steps={[
              {
                text: "Skicka in CID",
                element: <InputCid />
              },
              {
                text: "Hämta aktiveringskod",
                element: <EmailHasBeenSent />
              },
              {
                text: "Skapa konto",
                element: <InputDataAndCode />
              }
            ]}
            finishedElement={<CreationOfAccountFinished />}
          />
        </Fill>
        <Spacing />
      </div>
    );
  }
}

export default CreateAccount;
