import React from "react";

export const Switch = ({
  children,
  onChange,
  globalUniqueName,
  startValue = false
}) => (
  <label
    className="mdl-switch mdl-js-switch mdl-js-ripple-effect"
    htmlFor={globalUniqueName}
  >
    <input
      type="checkbox"
      id={globalUniqueName}
      className="mdl-switch__input"
      onChange={onChange}
      defaultChecked={startValue}
    />
    <span className="mdl-switch__label">{children}</span>
  </label>
);
