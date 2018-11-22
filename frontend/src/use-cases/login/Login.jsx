import React from "react";
import LoginForm from "./views/login-form";
import { DigitLayout } from "@cthit/react-digit-components";

class Login extends React.Component {
    constructor(props) {
        super();

        props.gammaLoadingFinished();
    }

    render() {
        const { login } = this.props;

        return (
            <DigitLayout.Margin>
                <DigitLayout.Center>
                    <LoginForm login={login} />
                </DigitLayout.Center>
            </DigitLayout.Margin>
        );
    }
}

export default Login;
