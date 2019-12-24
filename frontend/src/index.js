import React from "react";
import ReactDOM from "react-dom";

import App from "./app";

import ProvidersForApp from "./app/ProvidersForApp";

const Index = () => (
    <ProvidersForApp>
        <App />
    </ProvidersForApp>
);

ReactDOM.render(<Index />, document.getElementById("root"));
