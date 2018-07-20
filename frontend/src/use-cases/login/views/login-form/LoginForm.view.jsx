import React from "react";
import PropTypes from "prop-types";
import * as yup from "yup";

import {
  CIDInput,
  PasswordInput,
  LoginButton,
  CreateAccountButton,
  RememberMe
} from "./LoginForm.view.styles";

import GammaButton from "../../../../common/elements/gamma-button";
import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";

import { Center, MarginTop, Fill } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardSubTitle,
  GammaCardTitle,
  GammaLink
} from "../../../../common-ui/design";

const LoginForm = ({ text, login }) => (
  <Fill>
    <GammaForm
      validationSchema={yup.object().shape({
        cid: yup.string().required(text.FieldRequired),
        password: yup.string().required(text.FieldRequired),
        rememberMe: yup.boolean()
      })}
      initialValues={{ cid: "", password: "", rememberMe: false }}
      onSubmit={(values, actions) => {
        const data = {
          cid: values["cid"],
          password: values["password"]
        };

        login(
          data,
          values.rememberMe,
          text.SuccessfullLogin,
          text.IncorrectCidOrPassword,
          text.SomethingWentWrong
        );

        actions.resetForm();
      }}
      render={({ errors, touched }) => (
        <GammaCard absWidth="300px" absHeight="300px" hasSubTitle>
          <GammaCardTitle text={text.Login} />
          <GammaCardBody>
            <Center>
              <GammaFormField
                name="cid"
                component={CIDInput}
                componentProps={{
                  upperLabel: text.EnterYourCid
                }}
              />
              <GammaFormField
                name="password"
                component={PasswordInput}
                componentProps={{
                  upperLabel: text.EnterYourPassword,
                  password: true
                }}
              />

              <GammaFormField
                name="rememberMe"
                component={RememberMe}
                componentProps={{
                  label: text.RememberMe,
                  primary: true
                }}
              />
            </Center>
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <LoginButton text={text.Login} primary raised submit />
            <GammaLink to="/create-account">
              <CreateAccountButton text={text.CreateAccount} />
            </GammaLink>
          </GammaCardButtons>
        </GammaCard>
      )}
    />
  </Fill>
);

export default LoginForm;
