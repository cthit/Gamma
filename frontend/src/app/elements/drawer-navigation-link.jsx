import React from "react";
import styled from "styled-components";

import { NavLink } from "react-router-dom";
import { Padding } from "../../common-ui/layout";
import { Button } from "styled-mdl/lib/components/buttons";

export const DrawerNavigationLink = ({ children, link }) => (
  <DrawerNavigationLinkContainer>
    <Link className="mdl-navigation__link" to={link}>
      {children}
    </Link>
  </DrawerNavigationLinkContainer>
);

const DrawerNavigationLinkContainer = styled.div``;

const Link = styled(NavLink)`
  color: black !important;
  font-size: 17px;
  padding: 16px;
  text-decoration: none;
`;
