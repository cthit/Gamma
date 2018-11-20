import React from "react";
import { shallow } from "enzyme";
import GammaFormField from "./";

import { DigitCheckbox, DigitTextField } from "@cthit/react-digit-components";

describe("<GammaFormField/>", () => {
    test("Shallow render of <GammaFormField/> with a DigitTextField", () => {
        const wrapper = shallow(
            <GammaFormField
                name="password"
                component={DigitTextField}
                componentProps={{ upperLabel: "this is password yes" }}
            />
        );

        expect(wrapper).toMatchSnapshot();
    });

    test("Shallow render of <GammaFormField/> with a DigitCheckbox", () => {
        const wrapper = shallow(
            <GammaFormField
                name="userAgreement"
                component={DigitCheckbox}
                componentProps={{
                    primary: true,
                    label: "Jag accepterar användaravtalen"
                }}
            />
        );

        expect(wrapper).toMatchSnapshot();
    });
});
