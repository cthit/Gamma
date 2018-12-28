import React from "react";
import { withRouter } from "react-router";
import axios from "axios";

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
                        console.log(response);
                        localStorage.token = response.data;
                    });
            }
        }
    }

    render() {
        return null;
    }
}

export default withRouter(GammaIntegration);
