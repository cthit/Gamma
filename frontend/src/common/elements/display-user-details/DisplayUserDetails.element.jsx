import React from "react";
import {
    DigitButton,
    DigitDesign,
    DigitDisplayData,
    DigitTranslations
} from "@cthit/react-digit-components";
import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "../../../api/users/props.users.api";

import translations from "./DisplayUserDetails.element.translations";

function createKeysTexts(text) {
    const output = {};

    output[USER_CID] = text.cid;
    output[USER_FIRST_NAME] = text.firstName;
    output[USER_LAST_NAME] = text.lastName;
    output[USER_NICK] = text.nick;
    output[USER_EMAIL] = text.email;
    output[USER_ACCEPTANCE_YEAR] = text.acceptanceYear;

    return output;
}

function getUserPath(user, isMe) {
    if (isMe) {
        return "/me";
    }
    return "/users/" + user.id;
}

const DisplayUserDetails = ({ user, isMe }) => (
    <DigitTranslations
        translations={translations}
        render={text => (
            <DigitDesign.Card size={{ minWidth: "300px", maxWidth: "600px" }}>
                <DigitDesign.CardTitle
                    text={
                        user[USER_FIRST_NAME] +
                        " '" +
                        user[USER_NICK] +
                        "' " +
                        user[USER_LAST_NAME]
                    }
                />
                <DigitDesign.CardBody>
                    <DigitDisplayData
                        data={user}
                        keysText={createKeysTexts(text)}
                        keysOrder={[
                            USER_CID,
                            USER_FIRST_NAME,
                            USER_LAST_NAME,
                            USER_NICK,
                            USER_EMAIL,
                            USER_ACCEPTANCE_YEAR
                        ]}
                    />
                </DigitDesign.CardBody>
                <DigitDesign.CardButtons reverseDirection>
                    <DigitDesign.Link to={getUserPath(user, isMe) + "/edit"}>
                        <DigitButton text={text.Edit} primary raised />
                    </DigitDesign.Link>
                    <DigitDesign.Link
                        to={getUserPath(user, isMe) + "/change_password"}
                    >
                        <DigitButton
                            text={text.ChangePassword}
                            primary
                            raised
                        />
                    </DigitDesign.Link>
                </DigitDesign.CardButtons>
            </DigitDesign.Card>
        )}
    />
);

export default DisplayUserDetails;
