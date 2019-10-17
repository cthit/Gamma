import React from "react";
import styled from "styled-components";
import { IconButton, AppBar, Toolbar, Drawer } from "@material-ui/core";
import { Title } from "../common-ui/text";
import { Fill } from "../common-ui/layout";

export const StyledMenuButton = styled(IconButton)`
    /*Medium device (md)*/
    @media (min-width: 960px) {
        display: none;
    }
`;

export const StyledRoot = styled.div`
    min-height: 100vh;
    display: flex;
`;

export const StyledAppBar = styled(AppBar)`
    position: absolute;
    margin-left: 240px;

    /*Medium device (md)*/
    @media (min-width: 960px) {
        width: calc(100vw - 241px);
    }
`;

export const DeltaTitle = styled(Title)`
    align-self: center;
`;

export const HorizontalFill = styled.div`
    display: flex;
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    flex: 1;
`;

export const StyledToolbar = styled(Toolbar)`
    height: 64px;
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
    flex: 1;
    margin-top: 64px;

    /*Medium device (md)*/
    @media (min-width: 960px) {
        margin-left: 241px;
        width: calc(100vw - 241px);
    }
`;
