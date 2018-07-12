import React from "react";
import { shallow } from "enzyme";
import CreateAccount from "./CreateAccount";

describe("<CreateAccount/>", () => {
  test("Shallow render of <CreateAccount/>", () => {
    const wrapper = shallow(
      <CreateAccount location={{ pathname: "/" }} text={{}} />
    );
    expect(wrapper).toMatchSnapshot();
  });
});
