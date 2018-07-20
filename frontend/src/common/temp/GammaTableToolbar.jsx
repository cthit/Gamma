import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";

import classNames from "classnames";
import GammaTextField from "../elements/gamma-text-field";
import Toolbar from "@material-ui/core/Toolbar";
import { Text, Title } from "../../common-ui/text";
import { Spacing } from "../../common-ui/layout";

const Spacer = styled.div`
  flex: 1 1 100%;
`;

const TableTitle = styled(Title)`
  flex: 0 0 auto;
`;

const SearchInput = styled(GammaTextField)`
  width: 400px;
`;

const GammaTableToolbar = ({
  numSelected,
  searchInput,
  onSearchInputChange
}) => (
  <Toolbar>
    <TableTitle
      text={numSelected > 0 ? numSelected + " selected" : "Användare"}
    />
    <Spacer />
    <SearchInput
      upperLabel="Sök efter användare"
      value={searchInput}
      onChange={onSearchInputChange}
    />
    <Spacing />
  </Toolbar>
);

GammaTableToolbar.propTypes = {
  numSelected: PropTypes.number.isRequired
};

export default GammaTableToolbar;
