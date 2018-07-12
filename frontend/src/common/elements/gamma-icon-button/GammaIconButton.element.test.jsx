import React from "react";
import { shallow } from "enzyme";
import GammaIconButton from "./";
import { Add, MoreVert } from "@material-ui/icons";

describe("<GammaIconButton/>", () => {
  test("Shallow render of a primary <GammaIconButton/> with a Add icon", () => {
    const wrapper = shallow(
      <GammaIconButton primary component={Add} onClick={() => {}} />
    );

    expect(wrapper).toMatchSnapshot();
  });

  test("Shallow render of a secondary disabled<GammaIconButton/> with a MoreVert icon", () => {
    const wrapper = shallow(
      <GammaIconButton
        secondary
        disabled
        component={MoreVert}
        onClick={() => {}}
      />
    );

    expect(wrapper).toMatchSnapshot();
  });
});
