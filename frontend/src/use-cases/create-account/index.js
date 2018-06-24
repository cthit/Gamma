import React, { Component } from "react";
import { Route } from "react-router-dom";
import { connect } from "react-redux";
import InputDataAndCodeViewContainer from "./container/InputDataAndCodeViewContainer";
import InputCidViewContainer from "./container/InputCidViewContainer";
import CreationOfAccountFinishedView from "./views/creation-of-account-finished-view";
import EmailHasBeenSentView from "./views/email-has-been-sent-view";
import { Fill, Spacing } from "../../common-ui/layout";
import { GammaButton } from "../../common/gui/gamma-button";
import { GammaStepper } from "../../common/gui/gamma-stepper";

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
        return parseInt(step);
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
                element: <InputCidViewContainer />
              },
              {
                text: "Hämta aktiveringskod",
                element: <EmailHasBeenSentView />
              },
              {
                text: "Skapa konto",
                element: <InputDataAndCodeViewContainer />
              }
            ]}
            finishedElement={<CreationOfAccountFinishedView />}
          />
        </Fill>
        <Spacing />
      </div>
    );
  }
}

export default CreateAccount;
