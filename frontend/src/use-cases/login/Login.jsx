import React from "react";
import { Route } from "react-router-dom";
import LoginForm from "./elements/login-form";
import { DigitLayout } from "@cthit/react-digit-components";

import axios from "axios";

var called = false;

class Login extends React.Component {



    constructor(props) {
        super();

        props.gammaLoadingFinished();
    }

    render() {
        const { login } = this.props;

        const baseUrl = "http://localhost:8081/api/oauth/authorize";
        const responseType = "response_type=code";
        const clientId = "client_id=this_is_a_client_id";
        const redirectUri = "redirect_uri=http://localhost:3000/login";

        return (
            <div>
                <Route render={(props) => {
                    if(props.location.search !== ""){
                        const paramsResponse = new URLSearchParams(props.location.search);
                        const code = paramsResponse.get("code");
                        if(code){
                            if(this.called){
                                return null;
                            }
                            this.called = true;
                            axios.post("http://localhost:8082/auth", {
                                code: code
                            }).then(response => {
                                console.log(response);
                                localStorage.token = response.data;
                            });
                        }
                    }
                    console.log(props)
                    return null;
                }}/>
                <a href={baseUrl + "?" + responseType + "&" + clientId + "&" + redirectUri}>Logga in</a>
                </div>

        );
    }
}

export default Login;
