import React from "react";

export const TextField = ({ label, uniqueGlobalName }) => (
  <div className="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
    <input className="mdl-textfield__input" type="text" id={uniqueGlobalName} />
    <label className="mdl-textfield__label" htmlFor={uniqueGlobalName}>
      {label}
    </label>
  </div>
);
