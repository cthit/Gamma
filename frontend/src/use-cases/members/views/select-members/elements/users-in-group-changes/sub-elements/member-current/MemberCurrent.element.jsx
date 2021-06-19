import React from "react";

import { DigitLayout, DigitText } from "@cthit/react-digit-components";

import {
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "api/users/props.users.api";

const MemberCurrent = ({ member }) => (
    <DigitLayout.Column>
        <DigitText.Text
            text={
                member.user[USER_FIRST_NAME] +
                ' "' +
                member.user[USER_NICK] +
                '" ' +
                member.user[USER_LAST_NAME]
            }
        />
    </DigitLayout.Column>
);

MemberCurrent.defaultProps = {
    members: []
};

export default MemberCurrent;
