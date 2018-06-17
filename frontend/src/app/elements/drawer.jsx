import React from "react";

export const Drawer = ({ children, title }) => (
  <div className="mdl-layout__drawer">
    <span className="mdl-layout-title">{title}</span>
    <nav className="mdl-navigation">{children}</nav>
  </div>
);
