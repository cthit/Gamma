import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import registerServiceWorker from "./registerServiceWorker";
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import App from "./app/App";
import { ThemeProvider } from "styled-components";
import { initGlobals, createTheme, colors } from "styled-mdl";

import { configureStore } from "./redux/config/configStore";
import { createStore } from "redux";

initGlobals();

const theme = createTheme({
  colorPrimary: colors.indigo[500],
  colorPirmaryDark: colors.indigo[700],
  colorAccent: colors.pink[500],
  myCustomThemeColor: "#f5f5f5"
});

const store = createStore(configureStore);

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <Provider store={store}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </Provider>
  </ThemeProvider>,
  document.getElementById("root")
);
registerServiceWorker();
