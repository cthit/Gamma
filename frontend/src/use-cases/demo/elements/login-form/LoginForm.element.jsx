import React from "react";

import GammaTextField from "../../../../common/elements/gamma-text-field";

import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";
import GammaButton from "../../../../common/elements/gamma-button/GammaButton.element";
import GammaCheckbox from "../../../../common/elements/gamma-checkbox/GammaCheckbox.element";
import GammaSwitch from "../../../../common/elements/gamma-switch/GammaSwitch.element";

const LoginForm = ({}) => (
  <div>
    <GammaForm
      onSubmit={(values, actions) => {
        console.log(values);
        console.log(actions);
      }}
      initialValues={{
        user: "asdf",
        password: "fdas",
        userAgreement: true,
        mySwitch: false
      }}
      render={() => (
        <div>
          <GammaFormField
            name="user"
            component={GammaTextField}
            componentProps={{ lowerLabel: "User" }}
          />

          <GammaFormField
            name="password"
            component={GammaTextField}
            componentProps={{ upperLabel: "this is password yes" }}
          />

          <GammaFormField
            name="userAgreement"
            component={GammaCheckbox}
            componentProps={{
              primary: true,
              label: "Jag accepterar användaravtalen"
            }}
          />

          <GammaFormField
            name="mySwitch"
            component={GammaSwitch}
            componentProps={{
              label: "This is a switch",
              primary: true
            }}
          />

          <GammaButton text="Hej" primary raised submit />
        </div>
      )}
    />
  </div>
);

export default LoginForm;
/**
 *       <GammaTextField
        name="user"
        upperLabel="Hello lowerLabel"
        lowerLabel="Why hello upperLabel"
      />
      <GammaTextField
        name="password"
        upperLabel="Hello lowerLabel"
        lowerLabel="Why hello upperLabel"
      />
 */
