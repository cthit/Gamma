import React from "react";

import PropTypes from "prop-types";

import generateId from "../../utils/generateId";

import { FormControl, InputLabel, Input } from "@material-ui/core";

import GammaLowerLabel from "../../views/common/elements/gamma-lower-label";

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

/*

class GammaTextField extends React.Component {
  state = {
    currentText: this.props.startValue == null ? "" : this.props.startValue,
    lowerLabel: this.props.lowerLabelReflectLength
      ? this._getLowerLabelTextFromLength(
          this.props.startValue != null ? this.props.startValue : "",
          this.props.maxLength
        )
      : this.props.lowerLabel,
    inputId: generateId(),
    helperTextId: generateId(),
    error: false
  };

  invalidate(resetText) {
    if (this.props.validate != null) {
      const valid = this.props.validate(this.state.currentText);
      const text = resetText ? "" : this.state.currentText;
      this.setState({
        ...this.state,
        currentText: text,
        error: !valid
      });
    }
  }

  clearText() {
    this.setState({
      ...this.state,
      currentText: ""
    });
  }

  render() {
    const upperLabel =
      this.props.upperLabel != null
        ? this._generateUpperLabel(this.props.upperLabel, this.state.inputId)
        : null;

    const lowerLabelElement = this._generateLowerLabel(
      this.state.lowerLabel,
      this.state.helperTextId
    );

    const { maxLength, lowerLabel, lowerLabelReflectLength } = this.props;

    const { helperTextId, currentText } = this.state;

    return (
      <FormControl
        disabled={this.props.disabled}
        error={this.state.error}
        aria-describedby={this.state.helperTextId}
      >
        {upperLabel}
        <Input
          id={this.state.inputId}
          value={this.state.currentText}
          onChange={e => this._onChange(e)}
          placeholder={this.props.promptText}
          type={this._getInputType(this.props.password, this.props.numbersOnly)}
        />
        <GammaLowerLabel
          id={this.state.helperTextId}
          text={
            lowerLabelReflectLength
              ? currentText.length + "/" + maxLength
              : lowerLabel
          }
        />
      </FormControl>
    );
  }

  _onChange(e) {
    const maxLength = this.props.maxLength;

    const newValue = this._checkLength(e.target.value, maxLength);
    if (newValue !== e.target.value) {
      return;
    }

    const error = !this._validateIfNeeded(
      newValue,
      this.props.validate != null,
      this.props.validate
    );

    const lowerLabel = this._updateLowerLabelIfNeeded(
      newValue,
      maxLength,
      this.props.lowerLabelReflectLength,
      this.state.lowerLabel
    );

    this.props.onChange(newValue);
    this.setState({
      ...this.state,
      currentText: newValue,
      lowerLabel: lowerLabel,
      error: error
    });
  }

  _validateIfNeeded(newValue, needValidation, validateFunc) {
    return !needValidation || validateFunc(newValue);
  }

  _updateLowerLabelIfNeeded(
    newValue,
    maxLength,
    shouldUpdateLowerLabel,
    currentLowerLabel
  ) {
    return shouldUpdateLowerLabel
      ? this._getLowerLabelTextFromLength(newValue, maxLength)
      : currentLowerLabel;
  }

  _checkLength(value, maxLength) {
    if (maxLength == null || value == null) {
      return value;
    } else {
      if (value.length > maxLength) {
        value = value.substring(0, maxLength);
      }
      return value;
    }
  }

  _getLowerLabelTextFromLength(value, maxLength) {
    return value.length + "/" + maxLength;
  }

  _getInputType(password, numbersOnly) {
    return password ? "password" : numbersOnly ? "number" : "";
  }

  _generateUpperLabel(upperLabel, inputId) {
    return <InputLabel htmlFor={inputId}>{upperLabel}</InputLabel>;
  }

  _generateLowerLabel(lowerLabel, helperTextId) {
    return (
      <GammaLowerLabel
        id={helperTextId}
        text={lowerLabel == null ? "" : lowerLabel}
      />
    );
  }
}


GammaTextField.propTypes = {
  onChange: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  startValue: PropTypes.string,
  upperLabel: PropTypes.string,
  lowerLabel: PropTypes.string,
  numbersOnly: PropTypes.bool,
  password: PropTypes.bool,
  validate: PropTypes.func, //Must return a bool
  promptText: PropTypes.string,
  maxLength: PropTypes.number,
  lowerLabelReflectLength: PropTypes.bool
};

*/

export default GammaTextField;
