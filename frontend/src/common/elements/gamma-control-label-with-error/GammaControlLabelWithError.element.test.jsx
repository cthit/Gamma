import React from "react";
import { shallow } from "enzyme";
import GammaControlLabelWithError from "./";

describe("<GammaControlLabelWithError/>", () => {
  test("Shallow render of <GammaControlLabelWithError/> with error", () => {
    const wrapper = shallow(
      <GammaControlLabelWithError
        label="This is a gamma control label with error"
        error={true}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
