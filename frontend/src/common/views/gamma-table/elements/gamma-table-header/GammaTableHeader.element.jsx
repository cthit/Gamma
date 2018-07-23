import React from "react";
import styled from "styled-components";
import PropTypes from "prop-types";
import { Text, Title } from "../../../../../common-ui/text";

import {
  TableSortLabel,
  Grid,
  TableCell,
  TableRow,
  TableHead,
  Hidden
} from "@material-ui/core";

import IfElseRendering from "../../../../declaratives/if-else-rendering";

const StyledCheckboxTableCell = styled(TableCell)`
  padding-left: 24px;
`;

const GammaTableHeader = ({
  order,
  orderBy,
  numSelected,
  rowCount,
  headerTexts,
  columnsOrder,
  onRequestSort
}) => (
  <Hidden only="xs">
    <TableHead>
      <TableRow>
        <IfElseRendering
          test={headerTexts.__checkbox != null}
          ifRender={() => (
            <TableCell>
              <Text bold text={headerTexts.__checkbox} />
            </TableCell>
          )}
        />

        {columnsOrder.map(column => {
          return (
            <TableCell
              column={column}
              sortDirection={orderBy === column ? order : false}
            >
              <TableSortLabel
                active={orderBy === column}
                direction={order}
                onClick={event => onRequestSort(event, column)}
              >
                <Text bold text={headerTexts[column]} />
              </TableSortLabel>
            </TableCell>
          );
        })}

        <IfElseRendering
          test={headerTexts.__link != null}
          ifRender={() => (
            <TableCell>
              <Text bold text={headerTexts.__link} />
            </TableCell>
          )}
        />
      </TableRow>
    </TableHead>
  </Hidden>
);

GammaTableHeader.propTypes = {
  numSelected: PropTypes.number.isRequired,
  onRequestSort: PropTypes.func.isRequired,
  onSelectAllClick: PropTypes.func.isRequired,
  orderBy: PropTypes.string.isRequired,
  rowCount: PropTypes.number.isRequired
};

export default GammaTableHeader;
