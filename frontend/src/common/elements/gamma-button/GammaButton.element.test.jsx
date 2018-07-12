import React from "react";
import { shallow } from "enzyme";
import GammaButton from "./GammaButton.element";

describe("<GammaButton/>", () => {
  test("Shallow render of primary raised <GammaButton/>", () => {
    const wrapper = shallow(
      <GammaButton
        text="This is a button"
        onClick={() => console.log("I have been pressed")}
        primary
        raised
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of secondary disabled <GammaButton/>", () => {
    const wrapper = shallow(
      <GammaButton
        text="This is a button"
        onClick={() => console.log("I have been pressed")}
        secondary
        disabled
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of raised submit <GammaButton/>", () => {
    const wrapper = shallow(
      <GammaButton text="This is a button" raised submit />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
