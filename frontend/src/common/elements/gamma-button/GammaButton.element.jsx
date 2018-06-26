import React from "react";

import PropTypes from "prop-types";

import { Button } from "@material-ui/core";
import { getColor } from "../../views/common/utils/color";

const GammaButton = ({
  text,
  onClick,
  primary,
  secondary,
  raised,
  disabled
}) => (
  <Button
    onClick={onClick}
    disabled={disabled}
    color={getColor(primary, secondary)}
    variant={raised ? "contained" : "flat"}
  >
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

export default GammaButton;
