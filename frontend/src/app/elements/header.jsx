import React from "react";
import styled from "styled-components";

export const Header = ({ children, drawer, title }) => (
  <LayoutHeader className="mdl-layout__header">
    <div className="mdl-layout__header-row">
      <span className="mdl-layout-title">{title}</span>
      <div className="mdl-layout-spacer" />
    </div>
  </LayoutHeader>
);

const LayoutHeader = styled.header`
  > * {
    color: white !important;
  }
`;
