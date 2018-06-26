import React from "react";
import styled from "styled-components";

import { FormControlLabel } from "@material-ui/core";

const GammaControlLabelWithError = styled(({ error, ...props }) => (
  <FormControlLabel {...props} classes={{ label: "label" }} />
))`
  & .label {
    color: ${props => (props.error ? "#F44336" : "inherit")};
  }
`;

export default GammaControlLabelWithError;
