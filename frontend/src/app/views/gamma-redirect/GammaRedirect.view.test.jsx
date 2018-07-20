import React from "react";
import { shallow } from "enzyme";
import GammaRedirect from "./GammaRedirect.view";

describe("<GammaRedirect/>", () => {
  test("Shallow render of <GammaRedirect/>", () => {
    const wrapper = shallow(<GammaRedirect />);
    expect(wrapper).toMatchSnapshot();
  });
});
