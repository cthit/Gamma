import React from "react";
import styled from "styled-components";

import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableRow from "@material-ui/core/TableRow";
import Checkbox from "@material-ui/core/Checkbox";
import { withStyles } from "@material-ui/core/styles";

import { Text, Title } from "../../common-ui/text";

import GammaButton from "../elements/gamma-button";
import { GammaLink } from "../../common-ui/design";
import IfElseRendering from "../declaratives/if-else-rendering";

const StyledCheckbox = styled(Checkbox)`
  text-align: center;
`;

const StyledTableRow = styled(TableRow)`
  display: block;
  height: auto;
  margin-top: 10px;
  background-color: white;

  @media (min-width: 600px) {
    height: 48px;
    display: table-row;
    border: 0;
  }
`;

const StyledTableCell = styled(TableCell)`
  display: block;
  padding: 12px;
  font-size: 14px;
  text-align: right;
  border: 0;

  &:before {
    content: attr(datatitle);
    font-weight: 500;
    float: left;
    color: #000;
  }

  @media (min-width: 600px) {
    display: table-cell;
    padding: 20px 24px;
    font-size: 14px;
    text-align: left;
    border-bottom: 1px solid rgba(244, 244, 244, 1);

    &:before {
      content: "";
      display: none;
    }
  }

  //For some reason TableCell likes to do this.
  &:last-child {
    padding-right: 12px;
  }
`;

const GammaTableBody = ({
  page,
  rowsPerPage,
  data,
  classes,
  searchInput,
  isSelected,
  handleClick,
  rowShouldBeShown,
  headerTexts,
  columnsOrder
}) => (
  <TableBody>
    {data
      .filter(n => {
        return rowShouldBeShown(n);
      })
      .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
      .map(n => {
        const selected = isSelected(n.id);
        return (
          <StyledTableRow
            hover
            key={n.id}
            role="checkbox"
            aria-checked={selected}
            tabIndex={-1}
            selected={selected}
          >
            <IfElseRendering
              test={headerTexts.__checkbox != null}
              ifRender={() => (
                <StyledTableCell>
                  <StyledCheckbox
                    onClick={event => handleClick(event, n.id)}
                    checked={selected}
                  />
                </StyledTableCell>
              )}
            />

            {columnsOrder.map(column => (
              <StyledTableCell key={column} datatitle={headerTexts[column]}>
                <Text text={n[column]} />
              </StyledTableCell>
            ))}

            <IfElseRendering
              test={headerTexts.__link != null}
              ifRender={() => (
                <IfElseRendering
                  test={n.__link != null}
                  ifRender={() => (
                    <StyledTableCell datatitle={headerTexts.__link}>
                      <GammaLink to={n.__link}>
                        <GammaButton text="Detaljer" raised />
                      </GammaLink>
                    </StyledTableCell>
                  )}
                  elseRender={() => <StyledTableCell />}
                />
              )}
            />
          </StyledTableRow>
        );
      })}
  </TableBody>
);

export default GammaTableBody;
