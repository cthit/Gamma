import React from "react";
import { shallow } from "enzyme";
import InputCid from "./InputCid.view";

describe("<InputCid/>", () => {
    test("Shallow render of <InputCid/>", () => {
        const wrapper = shallow(<InputCid text={{}} sendCid={() => {}} />);
        expect(wrapper).toMatchSnapshot();
    });
});
