import React from "react";

import translations from "./WelcomeUser.element.translations.json";

import { DigitText, useDigitTranslations } from "@cthit/react-digit-components";

import { FIRST_NAME } from "../../../../api/users/props.users.api";

const WelcomeUser = ({ user }) => {
    const [text] = useDigitTranslations(translations);
    return (
        <>
            <DigitText.Heading2 text={text.Hi + " " + user[FIRST_NAME] + "!"} />
            <DigitText.Heading5 text={text.WelcomeToIT} />
        </>
    );
};

export default WelcomeUser;
