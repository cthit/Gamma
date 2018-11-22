import React from "react";
import { shallow } from "enzyme";
import InputDataAndCode from "./InputDataAndCode.view";

describe("<InputDataAndCode/>", () => {
    test("Shallow render of <InputDataAndCode/>", () => {
        const wrapper = shallow(
            <InputDataAndCode text={{}} sendDataAndCode={() => {}} />
        );
        expect(wrapper).toMatchSnapshot();
    });
});
