import React from "react";
import { shallow } from "enzyme";
import GammaStepper from "./";

describe("<GammaStepper/>", () => {
  test("Shallow render of <GammaStepper/>", () => {
    const wrapper = shallow(
      <GammaStepper
        activeStep={3}
        steps={[
          {
            text: "1",
            element: <h2>asdf</h2>
          },
          {
            text: "Det andra steget",
            element: <h2>second</h2>
          },
          {
            text: "Step 3",
            element: <h3>asdffdas</h3>
          }
        ]}
        finishedElement={<h1>Done</h1>}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
