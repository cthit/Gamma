import React from "react";
import { DigitProviders } from "@cthit/react-digit-components";
import { rootReducer } from "./App.reducer";
import commonTranslations from "../common/utils/translations/CommonTranslations.json";

const preloadedState = {};

const theme = {
  breakpoints: {
    xs: 0,
    sm: 600,
    md: 960,
    lg: 1280,
    xl: 1920
  }
};

export const ProvidersForApp = ({ children }) => (
  <DigitProviders
    commonTranslations={commonTranslations}
    rootReducer={rootReducer}
    preloadedState={preloadedState}
    defaultLanguage="sv"
    theme={theme}
  >
    {children}
  </DigitProviders>
);
