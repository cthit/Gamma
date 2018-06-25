import React from "react";

import PropTypes from "prop-types";

import { Radio, FormControlLabel, RadioGroup } from "@material-ui/core";

import GammaLowerLabel from "../common/elements/gamma-lower-label";

import {
  UpperLabel,
  StyledFormControl
} from "./GammaRadioButtonGroup.element.styles";
import generateId from "../../utils/generateId";
import { getColor } from "../common/utils/color";

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

GammaRadioButtonGroup.propTypes = {
  radioButtonMap: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  upperLabel: PropTypes.string,
  lowerLabel: PropTypes.string
};

export default GammaRadioButtonGroup;
