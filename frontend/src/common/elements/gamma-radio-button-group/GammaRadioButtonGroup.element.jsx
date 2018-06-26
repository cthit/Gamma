import React from "react";

import PropTypes from "prop-types";

import { Radio, FormControlLabel, RadioGroup } from "@material-ui/core";

import GammaLowerLabel from "../gamma-lower-label";

import {
  UpperLabel,
  StyledFormControl
} from "./GammaRadioButtonGroup.element.styles";
import { getColor } from "../../views/common/utils/color";

const GammaRadioButtonGroup = ({
  value,
  onChange,
  onBlur,
  name,
  upperLabel,
  lowerLabel,
  label,
  radioButtonMap
}) => (
  <StyledFormControl component="fieldset">
    <UpperLabel component="legend">{upperLabel}</UpperLabel>
    <RadioGroup value={value} name={name} onBlur={onBlur} onChange={onChange}>
      {Object.keys(radioButtonMap)
        .reverse()
        .map(value => {
          const { label, disabled, primary, secondary } = radioButtonMap[value];
          return (
            <FormControlLabel
              key={value}
              value={value}
              label={label}
              disabled={disabled}
              control={<Radio color={getColor(primary, secondary)} />}
            />
          );
        })}
    </RadioGroup>
    <GammaLowerLabel text={lowerLabel} />
  </StyledFormControl>
);

/*
class GammaRadioButtonGroup extends React.Component {
  state = {
    value: this.props.startValue != null ? this.props.startValue : ""
  };

  _handleChange = event => {
    this.setState({ value: event.target.value });
  };

  render() {
    return (
      <StyledFormControl component="fieldset">
        <UpperLabel component="legend">{this.props.upperLabel}</UpperLabel>
        <RadioGroup
          value={this.state.value}
          onChange={event => {
            this.props.onChange(event.target.value);
            this._handleChange(event);
          }}
        >
          {Object.keys(this.props.radioButtonMap)
            .reverse()
            .map(value => {
              const {
                label,
                disabled,
                primary,
                secondary
              } = this.props.radioButtonMap[value];
              return (
                <FormControlLabel
                  key={value}
                  value={value}
                  label={label}
                  disabled={disabled}
                  control={<Radio color={getColor(primary, secondary)} />}
                />
              );
            })}
        </RadioGroup>
        <GammaLowerLabel text={this.props.lowerLabel} />
      </StyledFormControl>
    );
  }
}
*/
GammaRadioButtonGroup.propTypes = {
  radioButtonMap: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  upperLabel: PropTypes.string,
  lowerLabel: PropTypes.string
};

export default GammaRadioButtonGroup;
