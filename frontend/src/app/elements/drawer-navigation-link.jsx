import React from "react";
import styled from "styled-components";

import { NavLink } from "react-router-dom";
import { ListItemText, ListItem } from "@material-ui/core";

export const DrawerNavigationLink = ({ children, link }) => (
  <Link to={link}>
    <ListItem button>
      <ListItemText>{children}</ListItemText>
    </ListItem>
  </Link>
);

const Link = styled(NavLink)`
  text-decoration: none;
`;
