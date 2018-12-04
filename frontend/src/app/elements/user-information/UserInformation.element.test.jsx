import React from "react";
import { shallow } from "enzyme";
import UserInformation from "./UserInformation.element";

describe("<UserInformation/>", () => {
    test("Shallow render of <UserInformation/>", () => {
        const wrapper = shallow(<UserInformation />);
        expect(wrapper).toMatchSnapshot();
    });
});
