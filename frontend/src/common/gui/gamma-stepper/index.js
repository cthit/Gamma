import React from "react";

import PropTypes from "prop-types";

import { Stepper, Step, StepLabel } from "@material-ui/core";

export class GammaStepper extends React.Component {
  state = {
    activeStep: 0,
    numberOfSteps: this.props.steps.length
  };

  setStep(step) {
    this.setState({
      ...this.state,
      activeStep: step
    });
  }

  nextStep() {
    this.setState({
      ...this.state,
      activeStep: Math.min(this.state.activeStep + 1, this.state.numberOfSteps)
    });
  }

  render() {
    return (
      <div>
        <Stepper activeStep={this.state.activeStep} alternativeLabel>
          {Object.keys(this.props.steps).map(index => {
            return (
              <Step key={this.props.steps[index].text}>
                <StepLabel>{this.props.steps[index].text}</StepLabel>
              </Step>
            );
          })}
        </Stepper>
        <div>
          {this.state.activeStep >= this.state.numberOfSteps
            ? this.props.finishedElement
            : this.props.steps[this.state.activeStep].element}
        </div>
      </div>
    );
  }
}
