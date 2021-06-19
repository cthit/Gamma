import React from "react";

import {
    DigitDesign,
    DigitLayout,
    useDigitTranslations
} from "@cthit/react-digit-components";

import HomeLink from "../../elements/home-link";
import translations from "./AdminOptions.element.translations.json";

const AdminOptions = () => {
    const [text] = useDigitTranslations(translations);

    return (
        <DigitDesign.Card>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={text.AdminOptions} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                <DigitLayout.Grid columns={`repeat(${2}, 1fr)`}>
                    <HomeLink
                        text={text.ActivationCodes}
                        link={"/activation-codes"}
                    />
                    <HomeLink text={text.GDPR} link={"/gdpr"} />
                    <HomeLink text={text.GroupPosts} link={"/posts"} />
                    <HomeLink text={text.Whitelist} link={"/whitelist"} />
                    <HomeLink text={text.Clients} link={"/clients"} />
                    <HomeLink text={text.SuperGroups} link={"/super-groups"} />
                    <HomeLink text={text.ApiKeys} link={"/access-keys"} />
                </DigitLayout.Grid>
            </DigitDesign.CardBody>
        </DigitDesign.Card>
    );
};

export default AdminOptions;
