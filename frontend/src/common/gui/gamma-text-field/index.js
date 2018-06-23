import React from "react";

import PropTypes from "prop-types";

import generateId from "../../utils/generateId";

import {
  FormControl,
  InputLabel,
  Input,
  FormHelperText
} from "@material-ui/core";

export class GammaTextField extends React.Component {
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

    const lowerLabel =
      this.props.lowerLabel != null || this.props.lowerLabelReflectLength
        ? this._generateLowerLabel(
            this.state.lowerLabel,
            this.state.helperTextId
          )
        : null;

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
          onChange={e => {
            e.target.value = this._checkLength(
              e.target.value,
              this.props.maxLength
            );

            const value = e.target.value;

            var error = false;
            if (this.props.validate != null) {
              error = !this.props.validate(value);
            }
            this.props.onChange(value);

            const lowerLabel = this.props.lowerLabelReflectLength
              ? this._getLowerLabelTextFromLength(value, this.props.maxLength)
              : this.state.lowerLabel;

            this.setState({
              ...this.state,
              currentText: value,
              lowerLabel: lowerLabel,
              error: error
            });
          }}
          placeholder={this.props.promptText}
          type={this._getInputType(this.props.password, this.props.numbersOnly)}
        />
        {lowerLabel}
      </FormControl>
    );
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
    return <FormHelperText id={helperTextId}>{lowerLabel}</FormHelperText>;
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
