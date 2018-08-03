import styled from "styled-components";

export const Fill = styled.div`
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: 1;

  display: flex;
  flex-direction: column;
  padding: 0px;
`;

export const VerticalFill = styled(Fill)`
  flex-direction: row;
  align-items: center;
`;

export const MarginTop = Fill.extend`
  margin: 8px;
`;

export const Margin = Fill.extend`
  margin: 4px; /*Half the standard margin*/
`;

export const Spacing = styled.div`
  width: 8px;
  height: 8px;
`;

export const Padding = Fill.extend`
  padding: 8px;
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: 1;
`;

export const FlexContainer = styled.div`
  display: flex;
`;

export const Center = styled.div`
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: 1;

  display: grid;
  grid-template-columns: auto;
  grid-template-rows: auto;
  justify-content: center;
  align-content: center;
`;

export const HideFill = styled(Fill)`
  display: ${props => (props.hidden ? "none" : "inherit")};
`;

export const Hide = styled.div`
  display: ${props => (props.hidden ? "none" : "inherit")};
`;

export const Size = styled.div`
  display: flex;

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
`;
