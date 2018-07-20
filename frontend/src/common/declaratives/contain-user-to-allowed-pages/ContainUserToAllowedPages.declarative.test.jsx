import React from "react";
import { shallow } from "enzyme";
import ContainUserToAllowedPages from "./ContainUserToAllowedPages.declarative";

describe("<ContainUserToAllowedPages/>", () => {
  test("Shallow render of <ContainUserToAllowedPages/>", () => {
    const wrapper = shallow(<ContainUserToAllowedPages />);
    expect(wrapper).toMatchSnapshot();
  });
});
