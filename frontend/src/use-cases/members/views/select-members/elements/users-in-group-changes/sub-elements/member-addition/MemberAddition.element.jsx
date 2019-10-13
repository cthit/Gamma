import React from "react";

import { DigitText } from "@cthit/react-digit-components";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../../../../../api/users/props.users.api";

const MemberAddition = ({ member }) => (
    <DigitText.Text
        text={
            member[FIRST_NAME] + ' "' + member[NICK] + '" ' + member[LAST_NAME]
        }
    />
);

export default MemberAddition;
