import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { FormControlLabel } from "@material-ui/core";

const GammaControlLabelWithError = styled(({ error, ...props }) => (
  <FormControlLabel {...props} classes={{ label: "label" }} />
))`
  & .label {
    color: ${props => (props.error ? "#F44336" : "inherit")};
  }
`;

GammaControlLabelWithError.propTypes = {
  error: PropTypes.bool
};

export default GammaControlLabelWithError;
