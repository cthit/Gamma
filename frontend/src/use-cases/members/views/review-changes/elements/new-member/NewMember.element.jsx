import React from "react";

import { DigitText } from "@cthit/react-digit-components";

const NewMember = ({
    firstName,
    lastName,
    nick,
    unofficialPostName,
    post = {},
    activeLanguage
}) => (
    <DigitText.Text
        text={
            firstName +
            " '" +
            nick +
            "' " +
            lastName +
            ": " +
            (activeLanguage === "sv" ? post.svText : post.enText) +
            " / " +
            unofficialPostName
        }
    />
);

export default NewMember;
