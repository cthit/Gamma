import React from "react";
import { shallow } from "enzyme";
import GammaCheckbox from "./GammaCheckbox.element";

describe("<GammaCheckbox/>", () => {
  test("Shallow render of a primary <GammaCheckbox/>", () => {
    const wrapper = shallow(
      <GammaCheckbox
        name="checkbox"
        value={false}
        onChange={e => console.log(e.target.value)}
        primary
        label="This is a primary checkbox"
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a disabled secondary <GammaCheckbox/> that is checked", () => {
    const wrapper = shallow(
      <GammaCheckbox
        name="secondary-checkbox"
        value={true}
        onChange={e => console.log(e.target.value)}
        secondary
        disabled
        label="This is a secondary checkbox"
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a <GammaCheckbox/> that has an error", () => {
    const wrapper = shallow(
      <GammaCheckbox
        name="error-checkbox"
        value={false}
        onChange={e => console.log(e.target.value)}
        onBlur={e => {}}
        error={true}
        label="This is a checkbox that has an error"
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a <GammaCheckbox/> that doesn't have a label", () => {
    const wrapper = shallow(
      <GammaCheckbox
        name="error-checkbox"
        value={false}
        onChange={e => console.log(e.target.value)}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
