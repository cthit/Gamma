import React from "react";
import { shallow } from "enzyme";
import GammaSwitch from "./";

describe("<GammaSwitch/>", () => {
  test("Shallow render of primary <GammaSwitch/>", () => {
    const wrapper = shallow(
      <GammaSwitch value={false} primary label="1" onChange={e => {}} />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of secondary <GammaSwitch/>", () => {
    const wrapper = shallow(
      <GammaSwitch value={true} secondary label="2" onChange={e => {}} />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of <GammaSwitch/> with a name", () => {
    const wrapper = shallow(
      <GammaSwitch
        value={false}
        label="3"
        name="Glass"
        onChange={e => {}}
        onBlur={e => {}}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a normal <GammaSwitch/> with an error", () => {
    const wrapper = shallow(
      <GammaSwitch value={true} label="4" error={true} onChange={e => {}} />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a disabled <GammaSwitch/>", () => {
    const wrapper = shallow(
      <GammaSwitch value={false} disabled label="5" onChange={e => {}} />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
