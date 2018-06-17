import React from "react";

export const IconButton = ({ iconName, onClick, idForMenu = "" }) => (
  <button
    id={idForMenu}
    className="mdl-button mdl-js-button mdl-button--icon"
    onClick={onClick}
  >
    <i className="material-icons">{iconName}</i>
  </button>
);
