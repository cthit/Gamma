import React from "react";

import {
    DigitButton,
    DigitDesign,
    DigitList,
    DigitText,
    useDigitDialog,
    useDigitToast,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { deleteAuthorityLevel } from "api/authorities/delete.authoritites";

import translations from "./AuthorityLevelCard.element.translations";
import AuthoritiesList from "../../../../authorities-list";

const AuthorityLevelCard = ({
    authorityLevel,
    forceUpdate,
    users = [],
    superGroups = [],
    posts = []
}) => {
    const [text] = useDigitTranslations(translations);
    const [openDialog] = useDigitDialog();
    const [queueToast] = useDigitToast();

    return (
        <DigitDesign.Card size={{ height: "500px" }}>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={authorityLevel} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                <AuthoritiesList
                    users={users}
                    posts={posts}
                    superGroups={superGroups}
                />
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons leftRight>
                <DigitDesign.Link to={"/authorities/edit/" + authorityLevel}>
                    <DigitButton text={text.Edit} outlined />
                </DigitDesign.Link>
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};

export default AuthorityLevelCard;
