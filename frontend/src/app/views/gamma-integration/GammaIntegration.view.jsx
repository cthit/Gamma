import React from "react";
import { withRouter } from "react-router";
import axios from "axios";

import * as p from "@cthit/react-digit-components";
console.log(p);

class GammaIntegration extends React.Component {
    constructor(props) {
        super(props);
        if (props.location.search !== "") {
            const paramsResponse = new URLSearchParams(props.location.search);
            const code = paramsResponse.get("code");
            if (code) {
                axios
                    .post("http://localhost:8082/auth", {
                        code: code
                    })
                    .then(response => {
                        localStorage.token = response.data;
                        props.redirectTo("/");
                    });
            }
        }
    }

    render() {
        return null;
    }
}

export default withRouter(GammaIntegration);
