import React from "react";

export const NumericTextField = ({ label, errorMsg, uniqueGlobalName }) => (
  <div className="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
    <input
      className="mdl-textfield__input"
      type="text"
      pattern="-?[0-9]*(\.[0-9]+)?"
      id={uniqueGlobalName}
    />
    <label className="mdl-textfield__label" htmlFor={uniqueGlobalName}>
      {label}
    </label>
    <span className="mdl-textfield__error">{errorMsg}</span>
  </div>
);
