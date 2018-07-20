import React from "react";
import styled from "styled-components";
import { Typography } from "@material-ui/core";

export const Display = styled(({ text, white, ...rest }) => (
  <Typography {...rest} variant="display2">
    {text}
  </Typography>
))`
  color: ${props => (props.white ? "white" : "")};
`;

export const Heading = styled(({ text, white, ...rest }) => (
  <Typography {...rest} variant="headline">
    {text}
  </Typography>
))`
  color: ${props => (props.white ? "white" : "")};
`;

export const Title = styled(({ text, white, ...rest }) => (
  <Typography {...rest} variant="title">
    {text}
  </Typography>
))`
  color: ${props => (props.white ? "white" : "")};
`;

export const Subtitle = styled(({ text, white, ...rest }) => (
  <Typography {...rest} variant="subheading">
    {text}
  </Typography>
))`
  color: ${props => (props.white ? "white" : "")};
`;

export const Text = styled(({ text, white, ...rest }) => (
  <Typography {...rest} variant="body1">
    {text}
  </Typography>
))`
  color: ${props => (props.white ? "white" : "")};
`;
