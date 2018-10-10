import React from "react";
import PropTypes from "prop-types";
import { FormControl, InputLabel, Input } from "@material-ui/core";

import GammaLowerLabel from "../gamma-lower-label";

import generateId from "../../utils/generators/id.generator";
import { Fill } from "../../../common-ui/layout";

const GammaTextField = ({
  value,
  onChange,
  onBlur,
  upperLabel,
  lowerLabel,
  name,
  password,
  numbersOnly,
  error,
  errorMessage,
  disabled
}) => (
  <Fill>
    <FormControl error={error}>
      <InputLabel>{upperLabel}</InputLabel>
      <Input
        name={name}
        value={value != null ? value : ""}
        onChange={onChange}
        onBlur={onBlur}
        type={password ? "password" : numbersOnly ? "number" : "text"}
        disabled={disabled}
      />
      <GammaLowerLabel
        text={
          errorMessage != null
            ? errorMessage
            : lowerLabel != null
              ? lowerLabel
              : ""
        }
      />
    </FormControl>
  </Fill>
);

GammaTextField.propTypes = {
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  onBlur: PropTypes.func,
  upperLabel: PropTypes.string,
  lowerLabel: PropTypes.string,
  name: PropTypes.string,
  password: PropTypes.bool,
  numbersOnly: PropTypes.bool,
  error: PropTypes.bool,
  errorMessage: PropTypes.string,
  disabled: PropTypes.bool
};

export default GammaTextField;
