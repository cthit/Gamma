import React from "react";
import { shallow } from "enzyme";
import IfElseRendering from "./IfElseRendering.declarative";

describe("<IfElseRendering/>", () => {
  test("Shallow render of <IfElseRendering/>", () => {
    const wrapper = shallow(
      <IfElseRendering
        test={true}
        ifRender={() => <div>hej</div>}
        elseRender={() => <div>då</div>}
      />
    );
    expect(wrapper).toMatchSnapshot();
  });
});
