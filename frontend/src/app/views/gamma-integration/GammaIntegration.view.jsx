import React from "react";
import { withRouter } from "react-router";
import axios from "axios";
import { getBackendUrl, getFrontendUrl } from "../../../common/utils/configs/envVariablesLoader";

class GammaIntegration extends React.Component {
    constructor(props) {
        super(props);
        if (props.location.search !== "") {
            const paramsResponse = new URLSearchParams(props.location.search);
            const code = paramsResponse.get("code");
            const id = "7hAdUEtMo4MgFnA7ZoZ41ohTe1NNRoJmjL67Gf0NIrrBnauyhc";
            const secret = "secret";

            const params = new URLSearchParams();
            params.append("grant_type", "authorization_code");
            params.append("client_id", id);
            params.append(
                "redirect_uri",
                getFrontendUrl() + "/login"
            );
            params.append("code", code);
            props.startedFetchingAccessToken();

            const path = getBackendUrl()

            const c = Buffer.from(id + ":" + secret).toString("base64");

            if (code) {
                axios
                    .post(path + "/oauth/token?" + params.toString(), null, {
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded",
                            Authorization: "Basic " + c
                        }
                    })
                    .then(response => {
                        localStorage.token = response.data.access_token;
                        props.userUpdateMe().then(() => {
                            props.redirectTo("/");
                            props.finishedFetchingAccessToken();
                        });
                    })
                    .catch(error => {
                        props.finishedFetchingAccessToken();
                    });
            }
        } else {
            props.redirectTo("/");
        }
    }

    render() {
        return null;
    }
}

export default withRouter(GammaIntegration);
