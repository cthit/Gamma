import React from "react";
import {
    DigitButton,
    DigitDesign,
    DigitDisplayData,
    DigitTranslations
} from "@cthit/react-digit-components";
import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../api/users/props.users.api";

import translations from "./DisplayUserDetails.element.translations";

function createKeysTexts(text) {
    const output = {};

    output[CID] = text.cid;
    output[FIRST_NAME] = text.firstName;
    output[LAST_NAME] = text.lastName;
    output[NICK] = text.nick;
    output[EMAIL] = text.email;
    output[ACCEPTANCE_YEAR] = text.acceptanceYear;

    return output;
}

function getUserPath(user, isMe) {
    if (isMe) {
        return "/me"
    }
    return "/users/" + user.id;
}

const DisplayUserDetails = ({ user, isMe}) =>

    (
    <DigitTranslations
        translations={translations}
        render={text => (
            <DigitDesign.Card minWidth="300px" maxWidth="600px">
                <DigitDesign.CardTitle
                    text={
                        user[FIRST_NAME] +
                        " '" +
                        user[NICK] +
                        "' " +
                        user[LAST_NAME]
                    }
                />
                <DigitDesign.CardBody>
                    <DigitDisplayData
                        data={user}
                        keysText={createKeysTexts(text)}
                        keysOrder={[
                            CID,
                            FIRST_NAME,
                            LAST_NAME,
                            NICK,
                            EMAIL,
                            ACCEPTANCE_YEAR
                        ]}
                    />
                </DigitDesign.CardBody>
                <DigitDesign.CardButtons reverseDirection>
                    <DigitDesign.Link to={getUserPath(user, isMe) + "/edit"}>
                        <DigitButton text={text.Edit} primary raised />
                    </DigitDesign.Link>
                    <DigitDesign.Link to={getUserPath(user, isMe) + "/change_password"}>
                        <DigitButton text={text.ChangePassword} primary raised/>
                    </DigitDesign.Link>
                </DigitDesign.CardButtons>
            </DigitDesign.Card>
        )}
    />
);

export default DisplayUserDetails;
