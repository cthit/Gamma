import React from "react";

import GammaTextField from "../../../../common/elements/gamma-text-field";

import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";

const LoginForm = ({}) => (
  <div>
    <GammaForm
      onSubmit={(values, actions) => {
        console.log(values);
        console.log(actions);
      }}
      initialValues={{ user: "asdf", password: "fdas" }}
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

          <button type="submit">Submit</button>
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
