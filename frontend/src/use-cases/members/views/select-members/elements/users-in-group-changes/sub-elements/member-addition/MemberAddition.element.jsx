import React from "react";

import { DigitText } from "@cthit/react-digit-components";
import {
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "../../../../../../../../api/users/props.users.api";

const MemberAddition = ({ member }) => (
    <DigitText.Text
        text={
            member[USER_FIRST_NAME] +
            ' "' +
            member[USER_NICK] +
            '" ' +
            member[USER_LAST_NAME]
        }
    />
);

export default MemberAddition;
