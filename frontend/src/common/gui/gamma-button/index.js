import React from "react";

import PropTypes from "prop-types";

import { Button } from "@material-ui/core";

export const GammaButton = ({
  text,
  onClick,
  primary,
  secondary,
  raised,
  disabled
}) => (
  <Button {..._translateProps(onClick, primary, secondary, raised, disabled)}>
    {text}
  </Button>
);

GammaButton.propTypes = {
  text: PropTypes.string.isRequired,
  onClick: PropTypes.func.isRequired,
  primary: PropTypes.bool,
  secondary: PropTypes.bool,
  raised: PropTypes.bool,
  disabled: PropTypes.bool
};

function _translateProps(onClick, primary, secondary, raised, disabled) {
  return {
    onClick: onClick,
    disabled: disabled,
    color: primary ? "primary" : secondary ? "secondary" : "default",
    variant: raised ? "contained" : "flat"
  };
}
