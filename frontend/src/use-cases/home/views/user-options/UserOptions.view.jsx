import React from "react";

import {
    DigitDesign,
    DigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";

import HomeLink from "../../elements/home-link";

import UserOptionsTranslations from "./UserOptions.view.translations.json";

class UserOptions extends React.Component {
    render() {
        return (
            <DigitTranslations
                translations={UserOptionsTranslations}
                uniquePath="Home.UserOptions"
                render={text => (
                    <DigitDesign.Card>
                        <DigitDesign.CardTitle text={text.UserOptions} />
                        <DigitDesign.CardBody>
                            <DigitLayout.Column marginVertical={"4px"}>
                                <HomeLink text="My account" link="/me" />
                                <HomeLink text="My groups" link="/me/groups" />
                            </DigitLayout.Column>
                        </DigitDesign.CardBody>
                    </DigitDesign.Card>
                )}
            />
        );
    }
}

export default UserOptions;
