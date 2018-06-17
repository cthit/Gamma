import React from "react";
import styled from "styled-components";

export const CheckBox = ({ children, onChange, startValue }) => (
  <CheckBoxLabelStyled
    className="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect"
    htmlFor="checkbox-1"
  >
    <input
      type="checkbox"
      id="checkbox-1"
      className="mdl-checkbox__input"
      defaultChecked={startValue}
      onChange={onChange}
    />
    <span className="mdl-checkbox__label">{children}</span>
  </CheckBoxLabelStyled>
);

const CheckBoxLabelStyled = styled.label`
  user-select: none;
`;
