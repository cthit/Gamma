import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";

import classNames from "classnames";
import GammaTextField from "../../../../elements/gamma-text-field";
import { Toolbar } from "@material-ui/core";
import { Fill } from "../../../../../common-ui/layout";
import { Text, Title } from "../../../../../common-ui/text";
import GammaTranslations from "../../../../../common/declaratives/gamma-translations";
import translations from "./GammaTableToolbar.element.translations.json";

const Spacer = styled.div`
  flex: 1 1 100%;
`;

const TableTitle = styled(Title)`
  flex: 0 0 auto;
`;

const SearchInput = styled(GammaTextField)`
  width: 400px;
  min-width: 400px !important;
`;

const StyledToolbar = styled(Toolbar)`
  display: flex;
  justify-content: space-between;
`;

const GammaTableToolbar = ({
  numSelected,
  searchInput,
  onSearchInputChange,
  titleText
}) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Common.View.GammaTable.Element.GammaTableToolbar"
    render={text => (
      <StyledToolbar>
        <Fill>
          <TableTitle
            text={numSelected > 0 ? numSelected + text.Selected : titleText}
          />
        </Fill>
        <Fill>
          <SearchInput
            upperLabel="Sök efter användare"
            value={searchInput}
            onChange={onSearchInputChange}
          />
        </Fill>
      </StyledToolbar>
    )}
  />
);

GammaTableToolbar.propTypes = {
  numSelected: PropTypes.number.isRequired
};

export default GammaTableToolbar;
