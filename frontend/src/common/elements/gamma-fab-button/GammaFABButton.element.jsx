import React from "react";

import PropTypes from "prop-types";

import { DownRightFABButton } from "./GammaFABButton.element.styles";
import { getColor } from "../../views/common/utils/color";

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
    color={getColor(primary, secondary)}
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
