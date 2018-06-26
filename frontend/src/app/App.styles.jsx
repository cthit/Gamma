import React from "react";
import styled from "styled-components";
import breakpoint from "styled-components-breakpoint";
import { IconButton, AppBar, Toolbar, Drawer } from "@material-ui/core";

export const StyledMenuButton = styled(IconButton)`
  ${breakpoint("md")`
        display:none;
    `};
`;

export const StyledRoot = styled.div``;

export const StyledAppBar = styled(AppBar)`
  position: "absolute";
  margin-left: 240px;
  ${breakpoint("md")`
     width: calc(100vw - 241px);
  `};
`;

export const StyledToolbar = styled(Toolbar)`
  padding-left: 16px;
`;

export const StyledDrawer = styled(({ ...rest }) => (
  <Drawer {...rest} classes={{ paper: "paper" }} />
))`
  & .paper {
    width: 240px;
  }
`;

export const StyledMain = styled.main`
  ${breakpoint("md")`
    margin-left: 241px;
    width: calc(100vw - 256px);
  `};
  height: calc(100vh - 64px);
  margin-top: 64px;
`;
