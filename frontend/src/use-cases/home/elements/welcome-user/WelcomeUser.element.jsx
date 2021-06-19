import React from "react";

import { DigitText, useDigitTranslations } from "@cthit/react-digit-components";

import { USER_NICK } from "api/users/props.users.api";

import translations from "./WelcomeUser.element.translations.json";

const WelcomeUser = ({ user }) => {
    const [text] = useDigitTranslations(translations);
    return (
        <>
            <DigitText.Heading2 text={text.Hi + " " + user[USER_NICK] + "!"} />
            <DigitText.Heading5 text={text.WelcomeToIT} />
        </>
    );
};

export default WelcomeUser;
