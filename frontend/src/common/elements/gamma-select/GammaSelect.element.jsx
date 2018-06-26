import React from "react";

import PropTypes from "prop-types";
import generateId from "../../utils/generators/id.generator";

import { Input, InputLabel, MenuItem } from "@material-ui/core";

import { StyledFormControl, StyledSelect } from "./GammaSelect.element.styles";
import GammaLowerLabel from "../gamma-lower-label";

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

/*
class GammaSelect extends React.Component {
  state = {
    selected: this.props.startValue != null ? this.props.startValue : "",
    inputId: generateId("selected")
  };

  render() {
    const upperLabel = this._generateUpperLabel(this.props.upperLabel);
    const lowerLabel = this._generateLowerLabel(this.props.lowerLabel);

    return (
      <StyledFormControl>
        {upperLabel}
        <StyledSelect
          onChange={e => {
            const value = e.target.value;
            this.setState({
              ...this.state,
              selected: value
            });
            this.props.onChange(value);
          }}
          disabled={this.props.disabled}
          displayEmpty={this.props.allowToChooseNone}
          value={this.state.selected}
          input={<Input id={this.state.inputId} name="selected" />}
        >
          {this.props.allowToChooseNone ? (
            <MenuItem value="">{""}</MenuItem>
          ) : null}

          {this._getValues(this.props.valueToTextMap).map(value => {
            const text = this.props.valueToTextMap[value];
            return (
              <MenuItem key={value} value={value}>
                {text}
              </MenuItem>
            );
          })}
        </StyledSelect>
        {lowerLabel}
      </StyledFormControl>
    );
  }

  _generateUpperLabel(upperLabel) {
    if (upperLabel == null) {
      return null;
    }

    return (
      <InputLabel htmlFor={this.state.inputId}>
        {this.props.upperLabel}
      </InputLabel>
    );
  }

  _generateLowerLabel(lowerLabel) {
    if (lowerLabel == null) {
      return null;
    }

    return ;
  }
}

*/

GammaSelect.propTypes = {
  onChange: PropTypes.func.isRequired,
  valueToTextMap: PropTypes.object.isRequired,
  disabled: PropTypes.bool,
  allowToChooseNone: PropTypes.bool,
  upperLabel: PropTypes.string,
  lowerLabel: PropTypes.string,
  startValue: PropTypes.string,
  reverse: PropTypes.bool
};

export default GammaSelect;
