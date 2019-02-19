import React from "react";

import {
    DigitDesign,
    DigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";

import AdminOptionsTranslations from "./AdminOptions.view.translations.json";
import HomeLink from "../../elements/home-link";

class AdminOptions extends React.Component {
    render() {
        return (
            <DigitTranslations
                translations={AdminOptionsTranslations}
                uniquePath="Home.AdminOptions"
                render={text => (
                    <DigitDesign.Card width="300px">
                        <DigitDesign.CardTitle text={text.AdminOptions} />
                        <DigitDesign.CardBody>
                            <DigitLayout.Grid
                                columns={`repeat(${2}, 1fr)`}
                                margin={"4px"}
                            >
                                <HomeLink text="All users" link={"/users"} />
                                <HomeLink text="All groups" link={"/groups"} />
                                <HomeLink
                                    text="Activation codes"
                                    link={"/activation-codes"}
                                />
                                <HomeLink
                                    text="GDPR certified"
                                    link={"/gdpr"}
                                />
                                <HomeLink text="Group posts" link={"/posts"} />
                                <HomeLink
                                    text="Possible websites"
                                    link={"/websites"}
                                />
                                <HomeLink
                                    text="Whitelist"
                                    link={"/whitelist"}
                                />
                            </DigitLayout.Grid>
                        </DigitDesign.CardBody>
                    </DigitDesign.Card>
                )}
            />
        );
    }
}

export default AdminOptions;
