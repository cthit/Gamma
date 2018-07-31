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
  headerTexts,
  titleText,
  searchText
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
            upperLabel={
              searchText + " " + _getAllPossibleThingsToSearchFor(headerTexts)
            }
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

function _getAllPossibleThingsToSearchFor(headerTexts) {
  var output = "(";

  const texts = Object.keys(headerTexts)
    .filter(key => !key.startsWith("__"))
    .map(key => headerTexts[key]);

  var first = true;

  for (const key in texts) {
    if (texts.hasOwnProperty(key)) {
      const element = texts[key];
      if (!first) {
        output = output.concat(", " + element);
      } else {
        first = false;
        output = output.concat(element);
      }
    }
  }

  output = output.concat(")");
  return output;
}

export default GammaTableToolbar;
