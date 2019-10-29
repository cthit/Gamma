import React from "react";

import {
    DigitDesign,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";

import HomeLink from "../../elements/home-link";

import UserOptionsTranslations from "./UserOptions.element.translations.json";

const UserOptions = ({ hasGroups }) => (
    <DigitTranslations
        translations={UserOptionsTranslations}
        render={text => (
            <DigitDesign.Card>
                <DigitDesign.CardTitle text={text.UserOptions} />
                <DigitDesign.CardBody>
                    <DigitLayout.Column marginVertical={"4px"}>
                        <HomeLink text={text.MyAccount} link="/me" />
                        {hasGroups && (
                            <HomeLink text={text.MyGroups} link="/me/groups" />
                        )}
                    </DigitLayout.Column>
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        )}
    />
);

export default UserOptions;
