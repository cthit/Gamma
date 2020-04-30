import React from "react";
import {
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./DisplayMembersTable.element.translations";

import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "../../../api/users/props.users.api";

function generateHeaderTexts(text) {
    const headerTexts = {};

    headerTexts[USER_FIRST_NAME] = text.FirstName;
    headerTexts[USER_LAST_NAME] = text.LastName;
    headerTexts[USER_CID] = text.Cid;
    headerTexts[USER_NICK] = text.Nick;
    headerTexts[USER_ACCEPTANCE_YEAR] = text.AcceptanceYear;
    headerTexts["postName"] = text.PostName;
    headerTexts["__link"] = text.Details;

    return headerTexts;
}

const DisplayMembersTable = ({ users, noUsersText, margin = "0px" }) => {
    const [text, activeLanguage] = useDigitTranslations(translations);

    return (
        <DigitTable
            margin={margin}
            titleText={text.Users}
            searchText={text.SearchForUsers}
            idProp={USER_CID}
            startOrderBy={USER_FIRST_NAME}
            columnsOrder={[USER_NICK, "postName"]}
            headerTexts={generateHeaderTexts(text)}
            data={users.map(user => {
                const officialPostName =
                    activeLanguage === "sv" ? user.post.sv : user.post.en;

                const unofficialPostName =
                    user.unofficialPostName === user.post.sv ||
                    user.unofficialPostName === user.post.en ||
                    user.unofficialPostName == null
                        ? ""
                        : " - " + user.unofficialPostName;

                console.log(user);

                return {
                    ...user,
                    postName: officialPostName + unofficialPostName,
                    __link: "/users/" + user.id
                };
            })}
            emptyTableText={noUsersText || text.NoUsers}
            startRowsPerPage={10}
        />
    );
};

DisplayMembersTable.defaultProps = {
    users: []
};

export default DisplayMembersTable;
