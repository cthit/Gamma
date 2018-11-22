import React from "react";
import { shallow } from "enzyme";
import CreationOfAccountFinished from "./CreationOfAccountFinished.view";

describe("<CreationOfAccountFinished/>", () => {
    test("Shallow render of <CreationOfAccountFinished/>", () => {
        const wrapper = shallow(
            <CreationOfAccountFinished
                text={{ LoginForTheFirstTime: "something" }}
            />
        );
        expect(wrapper).toMatchSnapshot();
    });
});
