import React from "react";

import LoginForm from "./views/login-form";

import { Display } from "../../common-ui/text";
import { Center, Fill, Margin } from "../../common-ui/layout";

const SignIn = ({ signIn }) => (
  <Margin>
    <Center>
      <LoginForm signIn={signIn} />
    </Center>
  </Margin>
);

export default SignIn;
