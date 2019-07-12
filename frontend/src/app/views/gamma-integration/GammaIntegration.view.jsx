import React from "react";
import { withRouter } from "react-router";
import axios from "axios";

class GammaIntegration extends React.Component {
    constructor(props) {
        super(props);
        if (props.location.search !== "") {
            const paramsResponse = new URLSearchParams(props.location.search);
            const code = paramsResponse.get("code");
            props.startedFetchingAccessToken();
            if (code) {
                axios
                    .post(
                        (process.env.REACT_APP_JWT_URL ||
                            "http://localhost:8082") + "/auth",
                        {
                            code: code
                        }
                    )
                    .then(response => {
                        localStorage.token = response.data;
                        props.userUpdateMe().then(() => {
                            props.redirectTo("/");
                            props.finishedFetchingAccessToken();
                        });
                    })
                    .catch(error => {
                        props.finishedFetchingAccessToken();
                    });
            }
        }
        else {
            props.redirectTo("/");
        }
    }

    render() {
        return null;
    }
}

export default withRouter(GammaIntegration);
