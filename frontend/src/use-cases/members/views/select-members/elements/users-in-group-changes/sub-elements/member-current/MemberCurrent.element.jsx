import React from "react";

import { DigitLayout, DigitText } from "@cthit/react-digit-components";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../../../../../../../api/users/props.users.api";

const MemberCurrent = ({ member }) => (
    <DigitLayout.Column>
        <DigitText.Text
            text={
                member[FIRST_NAME] +
                ' "' +
                member[NICK] +
                '" ' +
                member[LAST_NAME]
            }
        />
    </DigitLayout.Column>
);

MemberCurrent.defaultProps = {
    members: []
};

export default MemberCurrent;
