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
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={text.UserOptions} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                <DigitLayout.Grid columns={`repeat(${2}, 1fr)`} margin={"4px"}>
                    <HomeLink text={text.MyAccount} link="/me" />
                    {hasGroups && (
                        <HomeLink text={text.MyGroups} link="/me/groups" />
                    )}
                    <HomeLink
                        text={text.ChangePassword}
                        link={"/me/change-password"}
                    />
                    <HomeLink text={text.ChangeAvatar} link={"/me/avatar"} />
                    <HomeLink text={text.AllUsers} link={"/users"} />
                    <HomeLink text={text.AllGroups} link={"/groups"} />
                </DigitLayout.Grid>
            </DigitDesign.CardBody>
        </DigitDesign.Card>
    );
};

export default UserOptions;
