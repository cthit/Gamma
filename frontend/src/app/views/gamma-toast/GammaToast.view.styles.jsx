import React from "react";
import styled from "styled-components";
import { Snackbar } from "@material-ui/core";
import { Button } from "@material-ui/core";

export const ToastButton = styled(({ hide, ...rest }) => <Button {...rest} />)`
  display: ${props => (props.hide ? "none" : "block")};
`;

export const StyledSnackbar = styled(Snackbar)``;
