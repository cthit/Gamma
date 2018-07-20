import React from "react";
import { shallow } from "enzyme";
import App from "./App";

describe("<App/>", () => {
  test("Shallow render of <App/>", () => {
    const wrapper = shallow(<App />);
    expect(wrapper).toMatchSnapshot();
  });
});
