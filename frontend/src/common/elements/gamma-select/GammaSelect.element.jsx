import React from "react";
import PropTypes from "prop-types";
import { Input, InputLabel, MenuItem } from "@material-ui/core";

import { StyledFormControl, StyledSelect } from "./GammaSelect.element.styles";

import GammaLowerLabel from "../gamma-lower-label";

import generateId from "../../utils/generators/id.generator";

const GammaSelect = ({
  value,
  onChange,
  disabled,
  valueToTextMap,
  allowToChooseNone,
  name,
  upperLabel,
  lowerLabel,
  reverse,
  inputProps
}) => (
  <StyledFormControl>
    <InputLabel>{upperLabel}</InputLabel>
    <StyledSelect
      onChange={onChange}
      disabled={disabled}
      displayEmpty={allowToChooseNone}
      value={value}
      inputProps={{
        id: "id-" + name,
        name: name,
        ...inputProps
      }}
    >
      {allowToChooseNone ? (
        <MenuItem value="" name="Nothing">
          {""}
        </MenuItem>
      ) : null}

      {_getValues(valueToTextMap, reverse).map(value => {
        const text = valueToTextMap[value];
        return (
          <MenuItem name={value} key={value} value={value}>
            {text}
          </MenuItem>
        );
      })}
    </StyledSelect>
    <GammaLowerLabel text={lowerLabel} />
  </StyledFormControl>
);

function _getValues(valueToTextMap, reverse) {
  var result = Object.keys(valueToTextMap);

  if (reverse) {
    result.reverse();
  }

  return result;
}

GammaSelect.propTypes = {
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  valueToTextMap: PropTypes.object.isRequired,
  allowToChooseNone: PropTypes.bool,
  upperLabel: PropTypes.string,
  lowerLabel: PropTypes.string,
  reverse: PropTypes.bool,
  inputProps: PropTypes.object
};

export default GammaSelect;
