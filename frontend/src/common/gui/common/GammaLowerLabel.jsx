import React from "react";

import PropTypes from "prop-types";

import { FormHelperText } from "@material-ui/core";

export const GammaLowerLabel = ({ text }) => (
  <FormHelperText>{text}</FormHelperText>
);

GammaLowerLabel.propTypes = {
  text: PropTypes.string.isRequired
};
