import React from "react";
import styled from "styled-components";

export const Spinner = ({ singleColor, shouldSpinn = true }) => (
  <SpinnerStyledContainer shouldSpinn={shouldSpinn}>
    <div className={_generateClasses(singleColor)} />
  </SpinnerStyledContainer>
);

const SpinnerStyledContainer = styled.div`
  display: ${props => (props.shouldSpinn ? "flex" : "none")};
`;

function _generateClasses(singleColor) {
  return "mdl-spinner mdl-js-spinner is-active " + _getSingleColor(singleColor);
}

function _getSingleColor(singleColor) {
  return singleColor ? " mdl-spinner--single-color " : "";
}
