import React from "react";
import { shallow } from "enzyme";
import GammaSelect from "./";

describe("<GammaSelect/>", () => {
  test("Shallow render of <GammaSelect/>", () => {
    const wrapper = shallow(
      <GammaSelect
        onChange={value => {
          console.log(value);
        }}
        upperLabel="Favorite Icecream flavor"
        lowerLabel="Choose the best flavor"
        valueToTextMap={{
          chocolate: "Chocolate",
          vanilla: "Vanilla",
          strawberry: "Strawberry"
        }}
        allowToChooseNone
        value="Chocolate"
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
