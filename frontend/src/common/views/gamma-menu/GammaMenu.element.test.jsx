import React from "react";
import { shallow } from "enzyme";
import GammaMenu from "./GammaMenu.element";

describe("<GammaMenu/>", () => {
  test("Shallow render of primary raised <GammaMenu/>", () => {
    const wrapper = shallow(
      <GammaMenu
        onClick={value => {
          console.log(value + " has been selected");
        }}
        valueToTextMap={{
          first_option: "First option",
          second_option: "Second option"
        }}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
