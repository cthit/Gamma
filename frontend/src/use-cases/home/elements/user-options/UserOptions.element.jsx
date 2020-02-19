import React from "react";

import {
    DigitDesign,
    DigitLayout,
    useDigitTranslations
} from "@cthit/react-digit-components";

import HomeLink from "../../elements/home-link";

import translations from "./UserOptions.element.translations.json";

const UserOptions = ({ hasGroups }) => {
    const [text] = useDigitTranslations(translations);

    return (
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
    );
};

export default UserOptions;
