import React from "react";
import styled from "styled-components";

import { HeadingLevel2, HeadingLevel3 } from "../text";

import { Paper } from "@material-ui/core";

/** Props:
 * width, height, maxWidth, maxHeight, minWidth, minHeight
 * pliancy: Changes shadow when on hover and on clicked
 * absWidth, absHeight: If size, max and min will be the same. use this
 */

/*${shadow4dp()};*/

export const GammaCard = styled(
  ({
    absWidth,
    absHeight,
    minWidth,
    minHeight,
    maxWidth,
    maxHeight,
    hasSubTitle,
    hasIcon,
    ...rest
  }) => <Paper {...rest} />
)`
  display: flex;
  flex-direction: column;

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
        /*shadow48p();*/
      }
    }};
  }
  &:active {
    ${props => {
      if (props.pliancy) {
        /*shadow2dp();*/
      }
    }};
  }
`;

export const GammaCardHeader = styled.div`
  padding: 8px;
  display: grid;

  grid-template-columns: ${props =>
    props.hasIcon ? "40px auto 32px" : "0px auto 32px"}

  grid-template-rows: ${props =>
    props.hasSubTitle ? "33px 25px auto" : "33px 0px auto"};
`;

export const GammaCardHeaderImage = styled.img`
  grid-column-start: 1;
  grid-column-end: 4;
  grid-row-start: 3;
  grid-row-end: 4;

  width: 100%;
  height: 200px;
  object-fit: cover;
`;

export const GammaCardIcon = styled.img`
  grid-column-start: 1;
  grid-column-end: 2;
  grid-row-start: 1;
  grid-row-end: 3;

  width: 100%;
  height: 100%;
  object-fit: contain;
`;

export const GammaCardMenuContainer = styled.div`
  grid-column-start: 3;
  grid-column-end: 4;
  grid-row-start: 1;
  grid-row-end: 2;
`;

export const GammaCardTitle = styled(HeadingLevel2)`
  font-size: 20px;
  line-height: 33px;

  margin: 0;
  margin-top: 8px;
  margin-left: 8px;
  margin-right: 8px;

  grid-column-start: 2;
  grid-column-end: 3;
  grid-row-start: 1;
  grid-row-end: 2;
`;

export const GammaCardSubTitle = styled(HeadingLevel3)`
  font-size: 15px;
  line-height: 25px;

  margin: 0;
  margin-left: 8px;
  margin-right: 8px;

  grid-column-start: 2;
  grid-column-end: 4;
  grid-row-start: 2;
  grid-row-end: 3;

  color: ${({ theme }) => theme.textColorSecondary};
`;

export const GammaCardBody = styled.div`
  flex: 1;

  display: flex;
  flex-direction: column;
  padding: 8px;
`;

export const GammaCardButtons = styled.div`
  padding: 8px;
  min-height: 50px;
  height: 50px;
  max-height: 50px;

  display: flex;
  flex-direction: ${props => (props.reverseDirection ? "row-reverse" : "row")};

  align-items: center;
`;

export const Divider = styled.hr`
  width: 80%;
  display: block;
  margin-left: auto;
  margin-right: auto;
`;
