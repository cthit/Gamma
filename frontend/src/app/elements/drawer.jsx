import React from "react";
import styled from "styled-components";

import { Divider } from "../../common-ui/design";
import { UserInformation } from "./user-information";
import { Spacing } from "../../common-ui/layout";

export const Drawer = ({ children, title, userData, signedIn }) => (
  <div className="mdl-layout__drawer">
    <span className="mdl-layout-title">{title}</span>
    <Navigation className="mdl-navigation">
      <Spacing />
      <Divider />
      <Spacing />
      <UserInformation />
      <Spacing />
      <Divider />
      <Spacing />
      {children}
    </Navigation>
  </div>
);

const Navigation = styled.nav`
  margin: 0px !important;
  padding: 0px !important;
`;
