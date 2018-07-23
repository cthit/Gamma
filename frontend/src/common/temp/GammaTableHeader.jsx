import React from "react";
import styled from "styled-components";
import PropTypes from "prop-types";
import Hidden from "@material-ui/core/Hidden";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import Grid from "@material-ui/core/Grid";
import Checkbox from "@material-ui/core/Checkbox";
import Tooltip from "@material-ui/core/Tooltip";
import { withStyles } from "@material-ui/core/styles";
import { Text, Title } from "../../common-ui/text";
import TableSortLabel from "@material-ui/core/TableSortLabel";

import IfElseRendering from "../declaratives/if-else-rendering";

const StyledCheckboxTableCell = styled(TableCell)`
  padding-left: 24px;
`;

class GammaTableHeader extends React.Component {
  createSortHandler = property => event => {
    this.props.onRequestSort(event, property);
  };

  render() {
    const { order, orderBy, numSelected, rowCount, headerTexts } = this.props;

    return (
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

            {Object.keys(headerTexts)
              .filter(key => key !== "__link")
              .filter(key => key !== "__checkbox")
              .map(key => {
                return (
                  <TableCell
                    key={key}
                    sortDirection={orderBy === key ? order : false}
                  >
                    <Tooltip title="Sort" enterDelay={300}>
                      <TableSortLabel
                        active={orderBy === key}
                        direction={order}
                        onClick={this.createSortHandler(key)}
                      >
                        <Text bold text={headerTexts[key]} />
                      </TableSortLabel>
                    </Tooltip>
                  </TableCell>
                );
              }, this)}

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
  }
}

GammaTableHeader.propTypes = {
  numSelected: PropTypes.number.isRequired,
  onRequestSort: PropTypes.func.isRequired,
  onSelectAllClick: PropTypes.func.isRequired,
  orderBy: PropTypes.string.isRequired,
  rowCount: PropTypes.number.isRequired
};

export default GammaTableHeader;
