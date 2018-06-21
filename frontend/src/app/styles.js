import React from "react";
import styled from "styled-components";
import breakpoint from "styled-components-breakpoint";

import IconButton from "@material-ui/core/IconButton";
import Drawer from "@material-ui/core/Drawer";
import AppBar from "@material-ui/core/AppBar";

export const StyledMenuButton = styled(IconButton)`
  ${breakpoint("md")`
        display:none;
    `};
`;

export const StyledRoot = styled.div`
  flex-grow: 1;
  height: 430;
  overflow: "hidden";
  position: "relative";
  display: "flex";
  width: "100%";
`;

export const StyledAppBar = styled(AppBar)`
  position: "absolute";
  margin-left: 240px;
  ${breakpoint("md")`
     width: calc(100vw - 240px);
  `};
`;

export const StyledDrawer = styled(({ ...rest }) => (
  <Drawer {...rest} classes={{ paper: "paper" }} />
))`
  & .paper {
    width: 240px;
  }
`;

export const StyledMain = styled.main`
  display: flex;
  flex-direction: column;

  width: 100vw;
  ${breakpoint("md")`
    margin-left: 241px;
    width: calc(100vw - 241px);

  `};
  height: calc(100vh - 64px);
  margin-top: 64px;
`;
