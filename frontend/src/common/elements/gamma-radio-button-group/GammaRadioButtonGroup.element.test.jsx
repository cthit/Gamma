import React from "react";
import { shallow } from "enzyme";
import GammaRadioButtonGroup from "./";

describe("<GammaRadioButtonGroup/>", () => {
  test("Shallow render of <GammaRadioButtonGroup/>", () => {
    const wrapper = shallow(
      <GammaRadioButtonGroup
        onChange={value => {}}
        value="5000"
        upperLabel="Upperlabel"
        lowerLabel="Lowerlabel"
        radioButtonMap={{
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
        }}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
