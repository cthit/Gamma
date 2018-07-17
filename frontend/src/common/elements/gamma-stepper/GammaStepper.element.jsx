import React from "react";
import PropTypes from "prop-types";
import { Stepper, Step, StepLabel } from "@material-ui/core";
import { Fill } from "../../../common-ui/layout";

const GammaStepper = ({ activeStep, steps, finishedElement }) => (
  <Fill>
    <Stepper activeStep={activeStep} alternativeLabel>
      {Object.keys(steps).map(index => {
        return (
          <Step key={steps[index].text}>
            <StepLabel>{steps[index].text}</StepLabel>
          </Step>
        );
      })}
    </Stepper>
    {activeStep >= steps.length ? finishedElement : steps[activeStep].element}
  </Fill>
);

GammaStepper.propTypes = {
  steps: PropTypes.arrayOf(Object).isRequired,
  finishedElement: PropTypes.object.isRequired,
  activeStep: PropTypes.number.isRequired
};

export default GammaStepper;
