import React from "react";

import {
    DigitDesign,
    DigitTranslations,
    DigitButton,
    DigitLayout
} from "@cthit/react-digit-components";

import HomeLink from "../../elements/home-link";

import UserOptionsTranslations from "./UserOptions.view.translations.json";

class UserOptions extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <DigitTranslations
                translations={UserOptionsTranslations}
                uniquePath="Home.UserOptions"
                render={text => (
                    <DigitDesign.Card>
                        <DigitDesign.CardTitle text={text.UserOptions} />
                        <DigitDesign.CardBody>
                            <DigitLayout.Column>
                                <HomeLink text="My account" link="/hej" />
                                <HomeLink text="My groups" link="/hej" />
                            </DigitLayout.Column>
                        </DigitDesign.CardBody>
                    </DigitDesign.Card>
                )}
            />
        );
    }
}

export default UserOptions;
