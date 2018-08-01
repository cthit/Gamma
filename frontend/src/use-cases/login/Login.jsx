import React from "react";

import LoginForm from "./views/login-form";

import { Display } from "../../common-ui/text";
import { Center, Fill, Margin } from "../../common-ui/layout";

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
