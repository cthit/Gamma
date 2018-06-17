import React from "react";
import styled from "styled-components";

//color can be either: 'primary', 'secondary', 'none'
export const Button = ({ children, color, raised, onClick }) => (
  <ButtonStyled
    textColor={_getTextColor(color)}
    className={_generateClasses(color, raised)}
    onClick={onClick}
  >
    {children}
  </ButtonStyled>
);

function _generateClasses(color, raised) {
  return (
    "mdl-button mdl-js-button mdl-js-ripple-effect" +
    _getColor(color) +
    _getRaised(raised)
  );
}

function _getColor(color = "") {
  switch (color) {
    case "primary":
      return " mdl-button--colored ";
    case "secondary":
      return " mdl-button--accent ";
    default:
      return "";
  }
}

function _getRaised(raised = false) {
  return raised ? " mdl-button--raised " : "";
}

function _getTextColor(color) {
  switch (color) {
    case "primary":
      return "white";
    case "secondary":
      return "white";
    default:
      return "black";
  }
}

export const ButtonStyled = styled.button`
  color: ${props => props.textColor} !important;
`;
