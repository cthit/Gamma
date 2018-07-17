import React from "react";
import PropTypes from "prop-types";
import * as yup from "yup";
import { NavLink } from "react-router-dom";

import {
  CIDInput,
  PasswordInput,
  SignInButton,
  CreateAccountButton,
  RememberMe
} from "./LoginForm.view.styles";

import GammaButton from "../../../../common/elements/gamma-button";
import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";

import { Center, MarginTop } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardSubTitle,
  GammaCardTitle
} from "../../../../common-ui/design";

const LoginForm = ({ text, signIn }) => (
  <div>
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

        signIn(data, values.rememberMe, "Yay", "Nay");

        console.log(values);
        //TODO don't send rememberMe to backend
        actions.resetForm();
      }}
      render={({ errors, touched }) => (
        <GammaCard absWidth="300px" absHeight="300px" hasSubTitle>
          <GammaCardTitle text={text.SignIn} />
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
                  label: text.RememberMe
                }}
              />
            </Center>
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <SignInButton text={text.SignIn} primary raised submit />
            <NavLink to="/create-account">
              <CreateAccountButton text={text.CreateAccount} />
            </NavLink>
          </GammaCardButtons>
        </GammaCard>
      )}
    />
  </div>
);

export default LoginForm;
