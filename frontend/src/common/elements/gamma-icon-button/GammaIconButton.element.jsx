import React from "react";

import PropTypes from "prop-types";

import { IconButton } from "@material-ui/core";
import { getColor } from "../../views/common/utils/color";

const GammaIconButton = ({}) => (
  <IconButton
    disabled={disabled}
    onClick={onClick}
    color={getColor(this.props.primary, this.props.secondary)}
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
