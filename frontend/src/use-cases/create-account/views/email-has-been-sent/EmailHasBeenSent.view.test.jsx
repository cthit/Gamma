import React from "react";
import { shallow } from "enzyme";
import EmailHasBeenSent from "./EmailHasBeenSent.view";

describe("<EmailHasBeenSent/>", () => {
    test("Shallow render of <EmailHasBeenSent/>", () => {
        const wrapper = shallow(
            <EmailHasBeenSent
                text={{
                    Back: "Something",
                    HaveRecievedACode: "Something else"
                }}
            />
        );
        expect(wrapper).toMatchSnapshot();
    });
});
