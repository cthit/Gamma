import React from "react";
import { createStore, applyMiddleware } from "redux";
import { Provider } from "react-redux";
import { ThemeProvider } from "styled-components";
import { createLogger } from "redux-logger";
import thunkMiddleware from "redux-thunk";
import { LocalizeProvider } from "react-localize-redux";
import { create } from "jss";
import { createGenerateClassName, jssPreset } from "@material-ui/core/styles";
import JssProvider from "react-jss/lib/JssProvider";

import { rootReducer } from "./App.reducer";

const generateClassName = createGenerateClassName();
const jss = create(jssPreset());
jss.options.insertionPoint = "insertion-point-jss";

const loggerMiddleware = createLogger();

const preloadedState = {};

const store = createStore(
  rootReducer,
  preloadedState,
  applyMiddleware(thunkMiddleware, loggerMiddleware)
);

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
  <ThemeProvider theme={theme}>
    <LocalizeProvider store={store}>
      <JssProvider jss={jss} generateClassName={generateClassName}>
        <Provider store={store}>{children}</Provider>
      </JssProvider>
    </LocalizeProvider>
  </ThemeProvider>
);
