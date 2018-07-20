import React from "react";
import styled from "styled-components";

import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableRow from "@material-ui/core/TableRow";
import Checkbox from "@material-ui/core/Checkbox";
import { withStyles } from "@material-ui/core/styles";

import { Text, Title } from "../../common-ui/text";

const styles = theme => ({
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

const GammaTableBody = ({
  page,
  rowsPerPage,
  data,
  classes,
  searchInput,
  isSelected,
  handleClick,
  rowShouldBeShown
}) => (
  <TableBody>
    {data
      .filter(n => rowShouldBeShown(n.name))
      .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
      .map(n => {
        const selected = isSelected(n.id);
        return (
          <TableRow
            hover
            key={n.id}
            onClick={event => handleClick(event, n.id)}
            role="checkbox"
            aria-checked={selected}
            tabIndex={-1}
            selected={selected}
            classes={{
              root: classes.tableBodyRow
            }}
          >
            <TableCell
              padding="checkbox"
              classes={{ root: classes.tableCheckboxCell }}
            >
              <Checkbox checked={selected} />
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
);

export default withStyles(styles)(GammaTableBody);
