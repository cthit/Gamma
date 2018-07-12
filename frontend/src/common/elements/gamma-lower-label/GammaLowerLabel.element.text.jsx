import React from "react";
import { shallow } from "enzyme";
import GammaLowerLabel from "./";

describe("<GammaLowerLabel/>", () => {
  test("Shallow render of <GammaLowerLabel/>", () => {
    const wrapper = shallow(
      <GammaLowerLabel text="This is a gamma lower label" />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
