import React from "react";

import PropTypes from "prop-types";

import { Switch } from "@material-ui/core";

import { StyledFormControlLabel } from "./GammaSwitch.element.styles";
import { getColor } from "../common/utils/color";

class GammaSwitch extends React.Component {
  state = {
    checked: this.props.startValue != null ? this.props.startValue : false
  };

  _handleChange(value) {
    this.setState({
      ...this.state,
      checked: value
    });
  }

  render() {
    return (
      <StyledFormControlLabel
        error={this.props.error}
        control={
          <Switch
            checked={this.state.checked}
            disabled={this.props.disabled}
            color={getColor(this.props.primary, this.props.secondary)}
            onChange={event => {
              const value = event.target.checked;
              this._handleChange(value);
              this.props.onChange(value);
            }}
          />
        }
        label={this.props.label}
      />
    );
  }
}

GammaSwitch.propTypes = {
  onChange: PropTypes.func.isRequired,
  label: PropTypes.string,
  primary: PropTypes.bool,
  secondary: PropTypes.bool,
  disabled: PropTypes.bool,
  startValue: PropTypes.bool,
  error: PropTypes.bool
};

export default GammaSwitch;
