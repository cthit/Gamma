import React from "react";

import {
    DigitDesign,
    DigitTranslations,
    DigitButton,
    DigitLayout
} from "@cthit/react-digit-components";

import AdminOptionsTranslations from "./AdminOptions.view.translations.json";
import HomeLink from "../../elements/home-link";

class AdminOptions extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <DigitTranslations
                translations={AdminOptionsTranslations}
                uniquePath="Home.AdminOptions"
                render={text => (
                    <DigitDesign.Card width="300px">
                        <DigitDesign.CardTitle text={text.AdminOptions} />
                        <DigitDesign.CardBody>
                            <DigitLayout.Grid columns={2}>
                                <HomeLink text="All users" />
                                <HomeLink text="All groups" />
                                <HomeLink text="Activation codes" />
                                <HomeLink text="Gdpr certified" />
                                <HomeLink text="Group posts" />
                                <HomeLink text="Possible websites" />
                                <HomeLink text="Whitelist" />
                            </DigitLayout.Grid>
                        </DigitDesign.CardBody>
                    </DigitDesign.Card>
                )}
            />
        );
    }
}

export default AdminOptions;
