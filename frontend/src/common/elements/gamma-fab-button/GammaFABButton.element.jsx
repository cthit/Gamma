import React from "react";
import PropTypes from "prop-types";

import { DownRightFABButton } from "./GammaFABButton.element.styles";

const GammaFABButton = ({
  onClick,
  disabled,
  primary,
  secondary,
  submit,
  component
}) => (
  <DownRightFABButton
    variant="fab"
    type={submit ? "submit" : "button"}
    disabled={disabled}
    onClick={onClick}
    color={primary ? "primary" : secondary ? "secondary" : "default"}
  >
    {React.createElement(component, null)}
  </DownRightFABButton>
);

GammaFABButton.propTypes = {
  onClick: PropTypes.func,
  component: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  primary: PropTypes.bool,
  secondary: PropTypes.bool
};

export default GammaFABButton;
