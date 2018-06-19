import React from "react";
import styled from "styled-components";
import { Button, Icon, Menu } from "styled-mdl";

export const GammaCardMenu = ({ children }) => (
  <Menu
    bottomRight
    control={
      <IconButton>
        <Icon name="more_vert" />
      </IconButton>
    }
  >
    {children}
  </Menu>
);

//Using <Button icon></Button> didn't work as an argument.
const IconButton = Button.extend`
  border-radius: 50%;
  font-size: ${({ theme }) => theme.buttonFabFontSize + "px"};
  height: ${({ theme }) => theme.buttonIconSize + "px"};
  margin-left: 0;
  margin-right: 0;
  min-width: ${({ theme }) => theme.buttonIconSize + "px"};
  width: ${({ theme }) => theme.buttonIconSize + "px"};
  padding: 0;
  overflow: hidden;
  color: inherit;
  line-height: normal;
`;
