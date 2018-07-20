import React from "react";
import { shallow } from "enzyme";
import DrawerNavigationLink from "./DrawerNavigationLink.element";

describe("<DrawerNavigationLink/>", () => {
  test("Shallow render of <DrawerNavigationLink/>", () => {
    const wrapper = shallow(<DrawerNavigationLink />);
    expect(wrapper).toMatchSnapshot();
  });
});
