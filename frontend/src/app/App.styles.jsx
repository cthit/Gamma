import React from "react";
import styled from "styled-components";
import { IconButton, AppBar, Toolbar, Drawer } from "@material-ui/core";

export const StyledMenuButton = styled(IconButton)`
  /*Medium device (md)*/
  @media (min-width: 960px) {
    display: none;
  }
`;

export const StyledRoot = styled.div``;

export const StyledAppBar = styled(AppBar)`
  position: "absolute";
  margin-left: 240px;

  /*Medium device (md)*/
  @media (min-width: 960px) {
    width: calc(100vw - 241px);
  }
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
  display: flex;
  flex-direction: column;
  height: calc(100vh - 72px);
  margin-top: 64px;

  /*Medium device (md)*/
  @media (min-width: 960px) {
    margin-left: 241px;
    width: calc(100vw - 256px);
  }
`;
