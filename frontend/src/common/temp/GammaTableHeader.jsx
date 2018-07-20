import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";

import classNames from "classnames";
import GammaTextField from "../elements/gamma-text-field";
import Toolbar from "@material-ui/core/Toolbar";
import { lighten } from "@material-ui/core/styles/colorManipulator";
import { withStyles } from "@material-ui/core/styles";
import { Text, Title } from "../../common-ui/text";

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

const GammaTableHeader = ({
  numSelected,
  searchInput,
  onSearchInputChange,
  classes
}) => (
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
        value={searchInput}
        onChange={onSearchInputChange}
      />
    </div>
  </Toolbar>
);

const SearchInput = styled(GammaTextField)`
  width: 400px;
`;

GammaTableHeader.propTypes = {
  classes: PropTypes.object.isRequired,
  numSelected: PropTypes.number.isRequired
};

export default withStyles(toolbarStyles)(GammaTableHeader);
