import React from "react";

import {
    DigitDesign,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";

import AdminOptionsTranslations from "./AdminOptions.element.translations.json";
import HomeLink from "../../elements/home-link";

const AdminOptions = () => (
    <DigitTranslations
        translations={AdminOptionsTranslations}
        render={text => (
            <DigitDesign.Card>
                <DigitDesign.CardTitle text={text.AdminOptions} />
                <DigitDesign.CardBody>
                    <DigitLayout.Grid
                        columns={`repeat(${2}, 1fr)`}
                        margin={"4px"}
                    >
                        <HomeLink text={text.AllUsers} link={"/users"} />
                        <HomeLink text={text.AllGroups} link={"/groups"} />
                        <HomeLink
                            text={text.ActivationCodes}
                            link={"/activation-codes"}
                        />
                        <HomeLink text={text.GDPR} link={"/gdpr"} />
                        <HomeLink text={text.GroupPosts} link={"/posts"} />
                        <HomeLink
                            text={text.PossibleWebsites}
                            link={"/websites"}
                        />
                        <HomeLink text={text.Whitelist} link={"/whitelist"} />
                    </DigitLayout.Grid>
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        )}
    />
);

export default AdminOptions;
