import styled from "styled-components";

import { shadow2dp, shadow4dp, shadow8dp } from "styled-mdl";

/** Props:
 * width, height, maxWidth, maxHeight, minWidth, minHeight
 * pliancy: Changes shadow when on hover and on clicked
 * absWidth, absHeight: If size, max and min will be the same. use this
 */
export const GammaCard = styled.div`
  ${shadow4dp()};

  display: grid;

  grid-template-columns: auto;
  grid-template-rows: 50px auto 50px;

  width: ${props => (props.absWidth != null ? props.absWidth : props.width)};
  height: ${props =>
    props.absHeight != null ? props.absHeight : props.height};

  max-width: ${props =>
    props.absWidth != null ? props.absWidth : props.maxWidth};
  max-height: ${props =>
    props.absHeight != null ? props.absHeight : props.maxHeight};

  min-width: ${props =>
    props.absWidth != null ? props.absWidth : props.minWidth};
  min-height: ${props =>
    props.absHeight != null ? props.absHeight : props.minHeight};

  background-color: white;

  &:hover {
    ${props => {
      if (props.pliancy) {
        shadow8dp();
      }
    }};
  }
  &:active {
    ${props => {
      if (props.pliancy) {
        shadow2dp();
      }
    }};
  }
`;

export const AbsoluteCenter = styled.div`
  display: grid;
  grid-template-columns: auto;
  grid-template-rows: auto;
`;

export const GammaCardTitle = styled.h2`
  font-size: 20px;

  display: block;
  margin-left: 8px;
  margin-top: auto;
  margin-bottom: auto;
  margin-right: 0;
  padding: 0;
`;

export const GammaCardBody = styled.div`
  display: flex;
  flex-direction: column;
  padding: 8px;
`;

export const GammaCardButtons = styled.div`
  display: flex;
  flex-direction: row;

  justify-content: flex-end;
  align-items: center;
`;
