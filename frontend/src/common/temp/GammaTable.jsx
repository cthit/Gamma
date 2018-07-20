import React from "react";
import styled from "styled-components";
import classNames from "classnames";
import PropTypes from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
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

let counter = 0;
function createData(name, calories, fat, carbs, protein) {
  counter += 1;
  return { id: counter, name, calories, fat, carbs, protein };
}

const columnData = [
  {
    id: "name",
    numeric: false,
    disablePadding: false,
    label: "Dessert (100g serving)"
  },
  { id: "calories", numeric: true, disablePadding: false, label: "Calories" },
  { id: "fat", numeric: true, disablePadding: false, label: "Fat (g)" },
  { id: "carbs", numeric: true, disablePadding: false, label: "Carbs (g)" },
  { id: "protein", numeric: true, disablePadding: false, label: "Protein (g)" }
];

class EnhancedTableHead extends React.Component {
  createSortHandler = property => event => {
    this.props.onRequestSort(event, property);
  };

  render() {
    const {
      onSelectAllClick,
      order,
      orderBy,
      numSelected,
      rowCount
    } = this.props;

    return (
      <Hidden only="xs">
        <TableHead>
          <TableRow>
            <TableCell padding="checkbox">
              <Grid>
                <Checkbox
                  indeterminate={numSelected > 0 && numSelected < rowCount}
                  checked={numSelected === rowCount}
                  onChange={onSelectAllClick}
                />
              </Grid>
            </TableCell>

            {columnData.map(column => {
              return (
                <TableCell
                  key={column.id}
                  sortDirection={orderBy === column.id ? order : false}
                >
                  <Tooltip
                    title="Sort"
                    placement={column.numeric ? "bottom-end" : "bottom-start"}
                    enterDelay={300}
                  >
                    <TableSortLabel
                      active={orderBy === column.id}
                      direction={order}
                      onClick={this.createSortHandler(column.id)}
                    >
                      <Text text={column.label} />
                    </TableSortLabel>
                  </Tooltip>
                </TableCell>
              );
            }, this)}
          </TableRow>
        </TableHead>
      </Hidden>
    );
  }
}

EnhancedTableHead.propTypes = {
  numSelected: PropTypes.number.isRequired,
  onRequestSort: PropTypes.func.isRequired,
  onSelectAllClick: PropTypes.func.isRequired,
  order: PropTypes.string.isRequired,
  orderBy: PropTypes.string.isRequired,
  rowCount: PropTypes.number.isRequired
};

const toolbarStyles = theme => ({
  root: {
    paddingRight: theme.spacing.unit
  },
  highlight:
    theme.palette.type === "light"
      ? {
          color: theme.palette.secondary.main,
          backgroundColor: lighten(theme.palette.secondary.light, 0.85)
        }
      : {
          color: theme.palette.text.primary,
          backgroundColor: theme.palette.secondary.dark
        },
  spacer: {
    flex: "1 1 100%"
  },
  actions: {
    color: theme.palette.text.secondary
  },
  title: {
    flex: "0 0 auto"
  }
});

let EnhancedTableToolbar = props => {
  const { numSelected, classes } = props;

  return (
    <Toolbar
      className={classNames(classes.root, {
        [classes.highlight]: numSelected > 0
      })}
    >
      <div className={classes.title}>
        {numSelected > 0 ? (
          <Title text={numSelected + " selected"} />
        ) : (
          <Title text="Användare" />
        )}
      </div>
      <div className={classes.spacer} />
      <div className={classes.actions}>
        <SearchInput
          upperLabel="Sök efter användare"
          value={props.searchInput}
          onChange={props.onSearchInputChange}
        />
      </div>
    </Toolbar>
  );
};

const SearchInput = styled(GammaTextField)`
  width: 400px;
`;

EnhancedTableToolbar.propTypes = {
  classes: PropTypes.object.isRequired,
  numSelected: PropTypes.number.isRequired
};

EnhancedTableToolbar = withStyles(toolbarStyles)(EnhancedTableToolbar);

const styles = theme => ({
  root: {
    marginTop: theme.spacing.unit * 3
  },

  tableBodyRow: {
    //Small Screen
    display: "block",
    height: "auto",
    marginTop: 10,
    backgroundColor: "white",

    [theme.breakpoints.up("sm")]: {
      height: 48,
      display: "table-row",
      border: 0,
      backgroundColor: "#ffffff"
    }
  },

  tableCheckboxCell: {
    [theme.breakpoints.down("sm")]: {
      width: "999999px"
    }
  },

  tableBodyData: {
    display: "block",
    padding: 12,
    fontSize: 14,
    textAlign: "right",
    border: 0,

    // Adding each data table head from here
    "&:before": {
      content: "attr(datatitle)",
      float: "left",
      color: "#00000"
    },

    [theme.breakpoints.up("sm")]: {
      display: "table-cell",
      padding: "20px 24px",
      fontSize: 14,
      textAlign: "left",
      borderBottom: "1px solid rgba(224, 224, 224, 1)",

      "&:before": {
        content: "",
        display: "none"
      }
    }
  }
});

class EnhancedTable extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.state = {
      searchInput: "",
      order: "asc",
      orderBy: "calories",
      selected: [],
      data: [
        createData("Cupcake", 305, 3.7, 67, 4.3),
        createData("Donut", 452, 25.0, 51, 4.9),
        createData("Eclair", 262, 16.0, 24, 6.0),
        createData("Frozen yoghurt", 159, 6.0, 24, 4.0),
        createData("Gingerbread", 356, 16.0, 49, 3.9),
        createData("Honeycomb", 408, 3.2, 87, 6.5),
        createData("Ice cream sandwich", 237, 9.0, 37, 4.3),
        createData("Jelly Bean", 375, 0.0, 94, 0.0),
        createData("KitKat", 518, 26.0, 65, 7.0),
        createData("Lollipop", 392, 0.2, 98, 0.0),
        createData("Marshmallow", 318, 0, 81, 2.0),
        createData("Nougat", 360, 19.0, 9, 37.0),
        createData("Oreo", 437, 18.0, 63, 4.0)
      ].sort((a, b) => (a.calories < b.calories ? -1 : 1)),
      page: 0,
      rowsPerPage: 5
    };
    console.log(this.state.data);
  }

  onSearchInputChange = e => {
    this.setState({
      searchInput: e.target.value
    });
  };

  handleRequestSort = (event, property) => {
    const orderBy = property;
    let order = "desc";

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
    if (checked) {
      this.setState({ selected: this.state.data.map(n => n.id) });
      return;
    }
    this.setState({ selected: [] });
  };

  handleClick = (event, id) => {
    const { selected } = this.state;
    const selectedIndex = selected.indexOf(id);
    let newSelected = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, id);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(selected.slice(1));
    } else if (selectedIndex === selected.length - 1) {
      newSelected = newSelected.concat(selected.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selected.slice(0, selectedIndex),
        selected.slice(selectedIndex + 1)
      );
    }

    this.setState({ selected: newSelected });
  };

  handleChangePage = (event, page) => {
    this.setState({ page });
  };

  handleChangeRowsPerPage = event => {
    this.setState({ rowsPerPage: event.target.value });
  };

  isSelected = id => this.state.selected.indexOf(id) !== -1;

  rowShouldBeShown = name =>
    this.state.searchInput === "" ||
    name.toLowerCase().includes(this.state.searchInput.toLowerCase());

  render() {
    const { classes } = this.props;
    const { data, order, orderBy, selected, rowsPerPage, page } = this.state;
    const emptyRows =
      rowsPerPage - Math.min(rowsPerPage, data.length - page * rowsPerPage);

    return (
      <Paper className={classes.root}>
        <EnhancedTableToolbar
          numSelected={this.state.selected.length}
          searchInput={this.state.searchInput}
          onSearchInputChange={this.onSearchInputChange}
        />

        <div className={classes.tableWrapper}>
          <Table className={classes.table} aria-labelledby="tableTitle">
            <EnhancedTableHead
              numSelected={
                selected.filter(n => this.rowShouldBeShown(n.name)).length ///TODO OPTIMIZE
              }
              order={order}
              orderBy={orderBy}
              onSelectAllClick={this.handleSelectAllClick}
              onRequestSort={this.handleRequestSort}
              rowCount={data.length}
            />
            <TableBody>
              {data
                .filter(n => this.rowShouldBeShown(n.name))
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map(n => {
                  const isSelected = this.isSelected(n.id);
                  return (
                    <TableRow
                      hover
                      key={n.id}
                      onClick={event => this.handleClick(event, n.id)}
                      role="checkbox"
                      aria-checked={isSelected}
                      tabIndex={-1}
                      selected={isSelected}
                      classes={{
                        root: classes.tableBodyRow
                      }}
                    >
                      <TableCell
                        padding="checkbox"
                        classes={{ root: classes.tableCheckboxCell }}
                      >
                        <Checkbox checked={isSelected} />
                      </TableCell>
                      <TableCell
                        datatitle="Dessert (100g serving)"
                        classes={{ root: classes.tableBodyData }}
                      >
                        <Text text={n.name} />
                      </TableCell>
                      <TableCell
                        datatitle="Calories"
                        classes={{ root: classes.tableBodyData }}
                      >
                        <Text text={n.calories} />
                      </TableCell>
                      <TableCell
                        datatitle="Fat (g)"
                        classes={{ root: classes.tableBodyData }}
                      >
                        <Text text={n.fat} />
                      </TableCell>
                      <TableCell
                        datatitle="Carbs (g)"
                        classes={{ root: classes.tableBodyData }}
                      >
                        <Text text={n.carbs} />
                      </TableCell>
                      <TableCell
                        datatitle="Protein (g)"
                        classes={{ root: classes.tableBodyData }}
                      >
                        <Text text={n.protein} />
                      </TableCell>
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
        </div>

        <TablePagination
          component="div"
          count={data.filter(n => this.rowShouldBeShown(n.name)).length} //TODO OPTIMIZE
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

EnhancedTable.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(styles)(EnhancedTable);
