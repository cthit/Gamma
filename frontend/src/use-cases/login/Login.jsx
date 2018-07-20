import React from "react";

import LoginForm from "./views/login-form";

import { Display } from "../../common-ui/text";
import { Center, Fill, Margin } from "../../common-ui/layout";

const Login = ({ login }) => (
  <Margin>
    <Center>
      <LoginForm login={login} />
    </Center>
  </Margin>
);

export default Login;
