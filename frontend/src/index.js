import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import registerServiceWorker from "./registerServiceWorker";
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import App from "./app";
import { ThemeProvider } from "styled-components";

import { createStore, applyMiddleware } from "redux";
import thunkMiddleware from "redux-thunk";
import { createLogger } from "redux-logger";

import { rootReducer } from "./app/App.reducer";

import JssProvider from "react-jss/lib/JssProvider";
import { create } from "jss";
import { createGenerateClassName, jssPreset } from "@material-ui/core/styles";

const generateClassName = createGenerateClassName();
const jss = create(jssPreset());
jss.options.insertionPoint = "insertion-point-jss";

const theme = {
  breakpoints: {
    xs: 0,
    sm: 600,
    md: 960,
    lg: 1280,
    xl: 1920
  }
};

const loggerMiddleware = createLogger();

const preloadedState = {};

const store = createStore(
  rootReducer,
  preloadedState,
  applyMiddleware(thunkMiddleware, loggerMiddleware)
);

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <JssProvider jss={jss} generateClassName={generateClassName}>
      <Provider store={store}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </Provider>
    </JssProvider>
  </ThemeProvider>,
  document.getElementById("root")
);
registerServiceWorker();
