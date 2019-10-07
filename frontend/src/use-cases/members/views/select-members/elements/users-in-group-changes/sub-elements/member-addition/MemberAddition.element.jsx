import React from "react";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../../../../../../../api/users/props.users.api";

import { DigitText } from "@cthit/react-digit-components";

const MemberAddition = ({ member }) => (
    <DigitText.Text
        text={
            member[FIRST_NAME] + ' "' + member[NICK] + '" ' + member[LAST_NAME]
        }
    />
);

export default MemberAddition;
