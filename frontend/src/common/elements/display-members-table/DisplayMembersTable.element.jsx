import React from "react";
import {
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./DisplayMembersTable.element.translations";

import {
    ACCEPTANCE_YEAR,
    CID,
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../api/users/props.users.api";

function generateHeaderTexts(text) {
    const headerTexts = {};

    headerTexts[FIRST_NAME] = text.FirstName;
    headerTexts[LAST_NAME] = text.LastName;
    headerTexts[CID] = text.Cid;
    headerTexts[NICK] = text.Nick;
    headerTexts[ACCEPTANCE_YEAR] = text.AcceptanceYear;
    headerTexts["postName"] = text.PostName;
    headerTexts["__link"] = text.Details;

    return headerTexts;
}

const DisplayMembersTable = ({ users, noUsersText }) => {
    const [text, activeLanguage] = useDigitTranslations(translations);

    return (
        <DigitTable
            margin={"0px"}
            titleText={text.Users}
            searchText={text.SearchForUsers}
            idProp={CID}
            startOrderBy={FIRST_NAME}
            columnsOrder={[NICK, "postName", ACCEPTANCE_YEAR]}
            headerTexts={generateHeaderTexts(text)}
            data={users.map(user => {
                const officialPostName =
                    activeLanguage === "sv" ? user.post.sv : user.post.en;

                const unofficialPostName =
                    user.unofficialPostName === user.post.sv ||
                    user.unofficialPostName === user.post.en
                        ? ""
                        : " - " + user.unofficialPostName;

                return {
                    ...user,
                    postName: officialPostName + unofficialPostName,
                    __link: "/users/" + user.id
                };
            })}
            emptyTableText={noUsersText || text.NoUsers}
        />
    );
};

DisplayMembersTable.defaultProps = {
    users: []
};

export default DisplayMembersTable;
