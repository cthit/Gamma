import React from "react";
import { shallow } from "enzyme";
import GammaTextField from "./";

describe("<GammaTextField/>", () => {
  test("Shallow render of primary <GammaTextField/>", () => {
    const wrapper = shallow(
      <GammaTextField
        onChange={() => {}}
        value="Hej"
        upperLabel="Hello lowerLabel"
        lowerLabel="Why hello upperLabel"
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of secondary <GammaTextField/>", () => {
    const wrapper = shallow(
      <GammaTextField
        onChange={() => {}}
        value="glazz"
        promptText="AwesomeUsername123"
        maxLength={20}
        upperLabel="Your username"
        lowerLabelReflectLength
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of <GammaTextField/> with a name", () => {
    const wrapper = shallow(
      <GammaTextField
        onChange={() => {}}
        value="hmmm"
        upperLabel="Don't write a T"
        lowerLabel="Please dont"
        error={true}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a normal <GammaTextField/> with an error", () => {
    const wrapper = shallow(
      <GammaTextField
        onChange={() => {}}
        value="Hej"
        lowerLabel="Why hello upperLabel"
        disabled
      />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a disabled <GammaTextField/>", () => {
    const wrapper = shallow(
      <GammaTextField
        onChange={() => {}}
        upperLabel="Your supersecret password"
        value="secret"
        lowerLabel="Very secret"
        password
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
  test("Shallow render of a disabled <GammaTextField/>", () => {
    const wrapper = shallow(
      <GammaTextField
        onChange={() => {}}
        value="123"
        upperLabel="Only numbers"
        numbersOnly
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
