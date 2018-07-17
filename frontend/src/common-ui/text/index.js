import React from "react";
import styled from "styled-components";
import { Typography } from "@material-ui/core";

export const Display = styled(({ text, ...rest }) => (
  <Typography {...rest} variant="display2">
    {text}
  </Typography>
))``;

export const Heading = styled(({ text, ...rest }) => (
  <Typography {...rest} variant="headline">
    {text}
  </Typography>
))``;

export const Title = styled(({ text, ...rest }) => (
  <Typography {...rest} variant="title">
    {text}
  </Typography>
))``;

export const Subtitle = styled(({ text, ...rest }) => (
  <Typography {...rest} variant="subheading">
    {text}
  </Typography>
))``;

export const Text = styled(({ text, ...rest }) => (
  <Typography {...rest} variant="body1">
    {text}
  </Typography>
))``;
