import React from "react";
import PropTypes from "prop-types";
import { Switch } from "@material-ui/core";

import GammaControlLabelWithError from "../gamma-control-label-with-error";

const GammaSwitch = ({
  onChange,
  onBlur,
  value,
  error,
  disabled,
  primary,
  secondary,
  label,
  name
}) => (
  <GammaControlLabelWithError
    error={error}
    control={
      <Switch
        checked={value}
        disabled={disabled}
        color={primary ? "primary" : secondary ? "secondary" : "default"}
        onChange={onChange}
        name={name}
        onBlur={onBlur}
      />
    }
    label={label}
  />
);

GammaSwitch.propTypes = {
  onChange: PropTypes.func.isRequired,
  onBlur: PropTypes.func,
  value: PropTypes.bool.isRequired,
  error: PropTypes.bool,
  disabled: PropTypes.bool,
  primary: PropTypes.bool,
  secondary: PropTypes.bool,
  label: PropTypes.string,
  name: PropTypes.string
};

export default GammaSwitch;
