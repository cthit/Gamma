import React from "react";
import { Route } from "react-router-dom";
import styled from "styled-components";
import LoginForm from "./elements/login-form";
import {
    DigitLayout,
    DigitDesign,
    DigitButton
} from "@cthit/react-digit-components";

import axios from "axios";
import GammaIntegration from "./views/gamma-integration";

var called = false;

const NoStyleLink = styled.a`
    text-decoration: none;
    color: inherit;
`;

class Login extends React.Component {
    constructor(props) {
        super();

        props.gammaLoadingFinished();
    }

    render() {
        const { login } = this.props;

        const baseUrl = "http://localhost:8081/api/oauth/authorize";
        const responseType = "response_type=code";
        const clientId =
            "client_id=7hAdUEtMo4MgFnA7ZoZ41ohTe1NNRoJmjL67Gf0NIrrBnauyhc";
        const redirectUri = "redirect_uri=http://localhost:3000/login";

        return (
            <React.Fragment>
                <GammaIntegration />
                <NoStyleLink
                    href={
                        baseUrl +
                        "?" +
                        responseType +
                        "&" +
                        clientId +
                        "&" +
                        redirectUri
                    }
                >
                    <DigitButton primary raised text={"Logga in"} />
                </NoStyleLink>
            </React.Fragment>
        );
    }
}

export default Login;
