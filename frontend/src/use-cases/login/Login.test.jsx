import React from "react";
import { shallow } from "enzyme";
import Login from "./Login";

describe("<Login/>", () => {
  test("Shallow render of <Login/>", () => {
    const wrapper = shallow(<Login />);
    expect(wrapper).toMatchSnapshot();
  });
});
