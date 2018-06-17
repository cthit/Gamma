import React from "react";
import styled from "styled-components";

export const RadioButton = ({
  children,
  globalUniqueName,
  globalUniqueGroupName,
  startValue = false,
  onChange
}) => (
  <RadioButtonLabelStyled
    className="mdl-radio mdl-js-radio mdl-js-ripple-effect"
    htmlFor={globalUniqueName}
  >
    <input
      type="radio"
      id={globalUniqueName}
      className="mdl-radio__button"
      name={globalUniqueGroupName}
      value={globalUniqueName}
      onChange={onChange}
      defaultChecked={startValue}
    />
    <span className="mdl-radio__label">{children}</span>
  </RadioButtonLabelStyled>
);

const RadioButtonLabelStyled = styled.label`
  user-select: none;
`;
