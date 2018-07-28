import React from "react";
import styled from "styled-components";
import PropTypes from "prop-types";
import _ from "lodash";

import {
  Hidden,
  Grid,
  lighten,
  Tooltip,
  IconButton,
  Checkbox,
  Paper,
  Typography,
  Toolbar,
  TableSortLabel,
  TableRow,
  TablePagination,
  TableCell,
  TableBody,
  Table
} from "@material-ui/core";

import { Text, Title, Display } from "../../../common-ui/text";
import GammaTextField from "../../elements/gamma-text-field";
import IfElseRendering from "../../declaratives/if-else-rendering";
import GammaTableToolbar from "./elements/gamma-table-toolbar";
import GammaTableBody from "./elements/gamma-table-body";
import GammaTableHeader from "./elements/gamma-table-header";
import { Center, Padding } from "../../../common-ui/layout";

let counter = 0;
function createData(name, calories, fat, carbs, protein) {
  counter += 1;
  return { id: counter, name, calories, fat, carbs, protein };
}

class GammaTable extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.state = {
      searchInput: "",
      order: "asc",
      orderBy: props.startOrderBy,
      page: 0,
      rowsPerPage: 5,

      data: [],
      headerTexts: props.headerTexts,
      sort: props.sort,
      columnsOrder: props.columnsOrder,
      idProp: props.idProp
    };
  }

  componentDidMount() {
    if (this.props.data != null) {
      this.updateData();
    }
  }

  componentDidUpdate(prevProps) {
    if (
      !_.isEqual(prevProps.data.slice().sort(), this.props.data.slice().sort())
    ) {
      this.updateData();
    }
  }

  updateData() {
    this.setState({
      data: this.props.data.sort(
        (a, b) => (a[this.props.orderBy] < b[this.props.orderBy] ? -1 : 1)
      )
    });
  }

  onSearchInputChange = e => {
    this.setState({
      searchInput: e.target.value
    });
  };

  handleRequestSort = (event, property) => {
    const orderBy = property;
    var order = "desc";

    if (this.state.orderBy === property && this.state.order === "desc") {
      order = "asc";
    }

    const data =
      order === "desc"
        ? this.state.data.sort((a, b) => (b[orderBy] < a[orderBy] ? -1 : 1))
        : this.state.data.sort((a, b) => (a[orderBy] < b[orderBy] ? -1 : 1));

    this.setState({ data, order, orderBy });
  };

  handleSelectAllClick = (event, checked) => {
    console.log(checked);
    if (checked) {
      this.props.onSelectedUpdated(
        this.state.data.map(n => n[this.state.idProp])
      );
      return;
    }
    this.props.onSelectedUpdated([]);
  };

  handleClick = (event, id) => {
    var newSelected = this.props.selected.slice();
    const selectedIndex = newSelected.indexOf(id);

    if (selectedIndex === -1) {
      newSelected.push(id);
    } else {
      newSelected.splice(selectedIndex, 1);
    }

    this.props.onSelectedUpdated(newSelected);
  };

  handleChangePage = (event, page) => {
    this.setState({ page });
  };

  handleChangeRowsPerPage = event => {
    this.setState({ rowsPerPage: event.target.value });
  };

  isSelected = id =>
    this.props.selected == null
      ? false
      : this.props.selected.indexOf(id) !== -1;

  rowShouldBeShown = row =>
    row != null &&
    Object.keys(this.state.headerTexts).filter(
      key =>
        row[key] != null &&
        (row[key] + "")
          .toLowerCase()
          .includes(this.state.searchInput.toLowerCase())
    ).length > 0; //Can be optimized, escape if one result is found

  render() {
    const { selected, emptyTableText } = this.props;
    const { data, order, orderBy, rowsPerPage, page, headerTexts } = this.state;
    const emptyRows =
      rowsPerPage - Math.min(rowsPerPage, data.length - page * rowsPerPage);

    return (
      <Paper>
        <GammaTableToolbar
          numSelected={selected == null ? -1 : selected.length}
          searchInput={this.state.searchInput}
          onSearchInputChange={this.onSearchInputChange}
        />

        <Table aria-labelledby="tableTitle">
          <GammaTableHeader
            numSelected={
              selected == null
                ? -1
                : selected.filter(n => this.rowShouldBeShown(n)).length ///TODO OPTIMIZE
            }
            columnsOrder={this.state.columnsOrder}
            order={order}
            orderBy={orderBy}
            onSelectAllClick={this.handleSelectAllClick}
            onRequestSort={this.handleRequestSort}
            rowCount={data.length}
            headerTexts={headerTexts}
          />
          <IfElseRendering
            test={this.state.data.length > 0}
            ifRender={() => (
              <GammaTableBody
                idProp={this.state.idProp}
                columnsOrder={this.state.columnsOrder}
                page={this.state.page}
                rowsPerPage={this.state.rowsPerPage}
                data={this.state.data}
                isSelected={this.isSelected}
                handleClick={this.handleClick}
                rowShouldBeShown={this.rowShouldBeShown}
                headerTexts={headerTexts}
              />
            )}
            elseRender={() => (
              <TableBody>
                <tr>
                  <td colSpan="100">
                    <Center>
                      <Padding>
                        <Display text={emptyTableText} />
                      </Padding>
                    </Center>
                  </td>
                </tr>
              </TableBody>
            )}
          />
        </Table>

        <TablePagination
          component="div"
          count={data.filter(n => this.rowShouldBeShown(n)).length} //TODO OPTIMIZE
          rowsPerPage={rowsPerPage}
          page={page}
          backIconButtonProps={{
            "aria-label": "Previous Page"
          }}
          nextIconButtonProps={{
            "aria-label": "Next Page"
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />
      </Paper>
    );
  }
}

export default GammaTable;
