import React, { Component } from "react";
import { Route } from "react-router-dom";
import { connect } from "react-redux";
import InputDataAndCodeScreenContainer from "./container/InputDataAndCodeScreenContainer";
import InputCidScreenContainer from "./container/InputCidScreenContainer";
import CreationOfAccountFinishedScreen from "./screens/creation-of-account-finished-screen";
import EmailHasBeenSentScreen from "./screens/email-has-been-sent-screen";
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
                element: <InputCidScreenContainer />
              },
              {
                text: "Hämta aktiveringskod",
                element: <EmailHasBeenSentScreen />
              },
              {
                text: "Skapa konto",
                element: <InputDataAndCodeScreenContainer />
              }
            ]}
            finishedElement={<CreationOfAccountFinishedScreen />}
          />
        </Fill>
        <Spacing />
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => ({
  stage: state.createAccount.stage
});

const mapDispatchToProps = (dispatch, ownProps) => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CreateAccount);
