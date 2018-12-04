import React from "react";
import { shallow } from "enzyme";
import MapPathToStep from "./";

describe("<MapPathToStep/>", () => {
    test("Shallow render of primary raised <MapPathToStep/>", () => {
        const wrapper = shallow(
            <MapPathToStep
                currentPath="/glass"
                pathToStepMap={{ "/glass": 1, "/asdf": 2 }}
                render={step => {}}
            />
        );

        expect(wrapper).toMatchSnapshot();
    });
});
