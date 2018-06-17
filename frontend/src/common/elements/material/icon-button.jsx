import React from "react";

export const IconButton = ({ iconName, onClick, menuId = "" }) => (
  <button
    id={menuId}
    className="mdl-button mdl-js-button mdl-button--icon"
    onClick={onClick}
  >
    <i className="material-icons">{iconName}</i>
  </button>
);
