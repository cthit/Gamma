import React from "react";

import PropTypes from "prop-types";

import generateId from "../../utils/generateId";

import { FormControl, InputLabel, Input } from "@material-ui/core";

import GammaLowerLabel from "../../views/common/elements/gamma-lower-label";

const GammaTextField = ({
  value,
  onChange,
  upperLabel,
  lowerLabel,
  name,
  password,
  numbersOnly,
  error,
  inputProps
}) => (
  <FormControl error={error}>
    <InputLabel>{upperLabel}</InputLabel>
    <Input
      value={value != null ? value : ""}
      onChange={onChange}
      inputProps={{
        ...inputProps
      }}
    />
    <GammaLowerLabel
      type={password ? "password" : numbersOnly ? "number" : "text"}
      text={lowerLabel != null ? lowerLabel : ""}
    />
  </FormControl>
);

GammaTextField.propTypes = {
  onChange: PropTypes.func.isRequired,
  value: PropTypes.string.isRequired,
  disabled: PropTypes.bool,
  upperLabel: PropTypes.string,
  lowerLabel: PropTypes.string,
  numbersOnly: PropTypes.bool,
  password: PropTypes.bool
};

export default GammaTextField;
