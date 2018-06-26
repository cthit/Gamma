import React from "react";

import GammaTextField from "../../../../common/elements/gamma-text-field";

import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";
import GammaButton from "../../../../common/elements/gamma-button";
import GammaCheckbox from "../../../../common/elements/gamma-checkbox";
import GammaSwitch from "../../../../common/elements/gamma-switch";
import GammaSelect from "../../../../common/elements/gamma-select";
import GammaRadioButtonGroup from "../../../../common/elements/gamma-radio-button-group";

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
        mySwitch: false,
        icecream: "chocolate",
        number: "5000"
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

          <GammaFormField
            name="icecream"
            component={GammaSelect}
            componentProps={{
              upperLabel: "Favorite Icecream flavor",
              lowerLabel: "Choose the best flavor",
              valueToTextMap: {
                chocolate: "Chocolate",
                vanilla: "Vanilla",
                strawberry: "Strawberry"
              },
              allowToChooseNone: true
            }}
          />

          <GammaFormField
            name="number"
            component={GammaRadioButtonGroup}
            componentProps={{
              upperLabel: "Upperlabel",
              lowerLabel: "Lowerlabel",
              radioButtonMap: {
                "5000": {
                  primary: true,
                  label: "Primary"
                },
                "4000": {
                  secondary: true,
                  label: "Secondary"
                },
                "3000": {
                  label: "Default"
                },
                "2000": {
                  disabled: true,
                  label: "Disabled"
                }
              }
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
