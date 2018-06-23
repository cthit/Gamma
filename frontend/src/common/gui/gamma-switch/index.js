import React from "react";

import PropTypes from "prop-types";

import { Switch } from "@material-ui/core";

import { StyledFormControlLabel } from "./styles";

export class GammaSwitch extends React.Component {
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
            color={
              this.props.primary
                ? "primary"
                : this.props.secondary
                  ? "secondary"
                  : "default"
            }
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
