import React from "react";

import PropTypes from "prop-types";

import { IconButton } from "@material-ui/core";

const GammaIconButton = ({
  disabled,
  onClick,
  primary,
  secondary,
  component
}) => (
  <IconButton
    disabled={disabled}
    onClick={onClick}
    color={primary ? "primary" : secondary ? "secondary" : "default"}
  >
    {React.createElement(component, null)}
  </IconButton>
);

GammaIconButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  component: PropTypes.func.isRequired,
  primary: PropTypes.bool,
  secondary: PropTypes.bool,
  disabled: PropTypes.bool
};

export default GammaIconButton;
