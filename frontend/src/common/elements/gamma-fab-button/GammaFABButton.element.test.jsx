import React from "react";
import { shallow } from "enzyme";
import GammaFABButton from "./";
import { Add } from "@material-ui/icons";

describe("<GammaFABButton/>", () => {
  test("Shallow render of primary submit <GammaFABButton/>", () => {
    const wrapper = shallow(
      <GammaFABButton primary submit onClick={() => {}} component={Add} />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of secondary submit disabled <GammaFABButton/>", () => {
    const wrapper = shallow(
      <GammaFABButton secondary disabled submit component={Add} />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
