import React from "react";
import { Center, Margin } from "../../common-ui/layout";
import LoginForm from "./views/login-form";

class Login extends React.Component {
    constructor(props) {
        super();

        props.gammaLoadingFinished();
    }

    render() {
        const { login } = this.props;

        return (
            <Margin>
                <Center>
                    <LoginForm login={login} />
                </Center>
            </Margin>
        );
    }
}

export default Login;
