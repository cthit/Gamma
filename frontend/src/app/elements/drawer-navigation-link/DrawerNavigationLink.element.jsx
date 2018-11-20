import React from "react";
import styled from "styled-components";
import { NavLink } from "react-router-dom";
import { ListItemText, ListItem } from "@material-ui/core";

const DrawerNavigationLink = ({ children, link, onClick }) => (
    <Link to={link}>
        <ListItem button>
            <ListItemText onClick={onClick}>{children}</ListItemText>
        </ListItem>
    </Link>
);

const Link = styled(NavLink)`
    text-decoration: none;
`;

export default DrawerNavigationLink;
