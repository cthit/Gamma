import React from "react";
import { shallow } from "enzyme";
import GammaFormField from "./";
import GammaCheckbox from "../gamma-checkbox";
import GammaTextField from "../gamma-text-field";

describe("<GammaFormField/>", () => {
  test("Shallow render of <GammaFormField/> with a GammaTextField", () => {
    const wrapper = shallow(
      <GammaFormField
        name="password"
        component={GammaTextField}
        componentProps={{ upperLabel: "this is password yes" }}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of <GammaFormField/> with a GammaCheckbox", () => {
    const wrapper = shallow(
      <GammaFormField
        name="userAgreement"
        component={GammaCheckbox}
        componentProps={{
          primary: true,
          label: "Jag accepterar användaravtalen"
        }}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
