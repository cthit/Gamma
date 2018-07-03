import React from "react";
import PropTypes from "prop-types";
import { FormControlLabel, Checkbox } from "@material-ui/core";

import GammaControlLabelWithError from "../gamma-control-label-with-error";

const GammaCheckbox = ({
  name,
  value,
  onChange,
  onBlur,
  primary,
  secondary,
  disabled,
  label,
  error
}) => (
  <GammaControlLabelWithError
    error={error}
    label={label}
    disabled={disabled}
    control={
      <Checkbox
        color={primary ? "primary" : secondary ? "secondary" : "default"}
        checked={value}
        onChange={onChange}
        onBlur={onBlur}
        name={name}
      />
    }
  />
);

GammaCheckbox.propTypes = {
  onChange: PropTypes.func.isRequired,
  label: PropTypes.string,
  startValue: PropTypes.bool,
  primary: PropTypes.bool,
  secondary: PropTypes.bool,
  disabled: PropTypes.bool
};

export default GammaCheckbox;
