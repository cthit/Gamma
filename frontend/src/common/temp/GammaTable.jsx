import React from "react";
import styled from "styled-components";
import classNames from "classnames";
import PropTypes from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TablePagination from "@material-ui/core/TablePagination";
import TableRow from "@material-ui/core/TableRow";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import Checkbox from "@material-ui/core/Checkbox";
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import DeleteIcon from "@material-ui/icons/Delete";
import FilterListIcon from "@material-ui/icons/FilterList";
import { lighten } from "@material-ui/core/styles/colorManipulator";
import Grid from "@material-ui/core/Grid";
import Hidden from "@material-ui/core/Hidden";
import { Text, Title } from "../../common-ui/text";
import GammaTextField from "../elements/gamma-text-field";
import IfElseRendering from "../declaratives/if-else-rendering";
import GammaTableToolbar from "./GammaTableToolbar";
import GammaTableBody from "./GammaTableBody";
import GammaTableHeader from "./GammaTableHeader";

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

      //This means that GammaTable has to be recreated to enter new data.
      //This may need to be fixed in the future.
      data: props.data,
      headerTexts: props.headerTexts,
      sort: props.sort
    };
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

    const { sort, data } = this.state;

    console.log(data);
    console.log(order);

    const newData =
      order === "desc"
        ? data.sort(sort[orderBy])
        : data.sort((a, b) => sort[orderBy](b, a));

    console.log(data);

    this.setState({ newData, order, orderBy });
  };

  handleSelectAllClick = (event, checked) => {
    if (checked) {
      this.setState({ selected: this.state.data.map(n => n.id) });
      return;
    }
    this.setState({ selected: [] });
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

  isSelected = id => this.props.selected.indexOf(id) !== -1;

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
    const { selected } = this.props;
    const { data, order, orderBy, rowsPerPage, page, headerTexts } = this.state;
    const emptyRows =
      rowsPerPage - Math.min(rowsPerPage, data.length - page * rowsPerPage);

    console.log(data.length);

    return (
      <Paper>
        <GammaTableToolbar
          numSelected={selected.length}
          searchInput={this.state.searchInput}
          onSearchInputChange={this.onSearchInputChange}
        />

        <Table aria-labelledby="tableTitle">
          <GammaTableHeader
            numSelected={
              selected.filter(n => this.rowShouldBeShown(n)).length ///TODO OPTIMIZE
            }
            order={order}
            orderBy={orderBy}
            onSelectAllClick={this.handleSelectAllClick}
            onRequestSort={this.handleRequestSort}
            rowCount={data.length}
            headerTexts={headerTexts}
          />
          <GammaTableBody
            page={this.state.page}
            rowsPerPage={this.state.rowsPerPage}
            data={this.state.data}
            isSelected={this.isSelected}
            handleClick={this.handleClick}
            rowShouldBeShown={this.rowShouldBeShown}
            headerTexts={headerTexts}
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
