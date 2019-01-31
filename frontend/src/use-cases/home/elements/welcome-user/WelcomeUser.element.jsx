import React from "react";

import WelcomeUserTranslations from "./WelcomeUser.element.translations.json";

import { DigitTranslations, DigitText } from "@cthit/react-digit-components";

import { FIRST_NAME } from "../../../../api/users/props.users.api";

const WelcomeUser = ({ user }) => (
    <DigitTranslations
        uniquePath="Home.WelcomeUser"
        translations={WelcomeUserTranslations}
        render={text => (
            <DigitText.Heading2 text={text.Hi + " " + user[FIRST_NAME] + "!"} />
        )}
    />
);

export default WelcomeUser;
