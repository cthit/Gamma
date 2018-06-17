import React from "react";

export const Menu = ({ menuId, children }) => (
  <ul
    className="mdl-menu mdl-menu--bottom-left mdl-js-menu mdl-js-ripple-effect"
    htmlFor={menuId}
  >
    {children}
  </ul>
);
