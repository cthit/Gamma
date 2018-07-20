import React from "react";
import { shallow } from "enzyme";
import GammaToast from "./GammaToast.view";

describe("<GammaToast/>", () => {
  test("Shallow render of <GammaToast/>", () => {
    const wrapper = shallow(<GammaToast />);
    expect(wrapper).toMatchSnapshot();
  });
});
