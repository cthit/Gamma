import React from "react";

import { DigitLayout, DigitText } from "@cthit/react-digit-components";
import {
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "../../../../../../../../api/users/props.users.api";

const MemberCurrent = ({ member }) => (
    <DigitLayout.Column>
        <DigitText.Text
            text={
                member[USER_FIRST_NAME] +
                ' "' +
                member[USER_NICK] +
                '" ' +
                member[USER_LAST_NAME]
            }
        />
    </DigitLayout.Column>
);

MemberCurrent.defaultProps = {
    members: []
};

export default MemberCurrent;
