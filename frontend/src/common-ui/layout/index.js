import styled from "styled-components";

export const Fill = styled.div`
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: 1;
  flex-direction: column;

  display: flex;
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
