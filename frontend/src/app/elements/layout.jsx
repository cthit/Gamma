import React from "react";

export const Layout = ({ children }) => (
  <div
    className="mdl-layout mdl-js-layout mdl-layout--fixed-drawer
  mdl-layout--fixed-header"
  >
    {children}
  </div>
);
