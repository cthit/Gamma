import React from "react";
import ReactDOM from "react-dom";

import App from "./app";

import registerServiceWorker from "./registerServiceWorker";
import { ProvidersForApp } from "./app/ProvidersForApp";

export const Index = () => (
  <ProvidersForApp>
    <App />
  </ProvidersForApp>
);

ReactDOM.render(<Index />, document.getElementById("root"));
registerServiceWorker();
