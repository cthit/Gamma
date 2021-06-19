import React from "react";
import ReactDOM from "react-dom";

import App from "./app";
import ProvidersForApp from "./app/ProvidersForApp";

const Index = () => (
    <ProvidersForApp>
        <React.StrictMode>
            <App />
        </React.StrictMode>
    </ProvidersForApp>
);

ReactDOM.render(<Index />, document.getElementById("root"));
