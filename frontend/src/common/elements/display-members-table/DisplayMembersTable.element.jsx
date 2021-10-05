import React from "react";

import {
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";

import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK
} from "api/users/props.users.api";

import translations from "./DisplayMembersTable.element.translations";

function generateHeaderTexts(text) {
    const headerTexts = {};

    headerTexts[USER_FIRST_NAME] = text.FirstName;
    headerTexts[USER_LAST_NAME] = text.LastName;
    headerTexts[USER_CID] = text.Cid;
    headerTexts[USER_NICK] = text.Nick;
    headerTexts[USER_ACCEPTANCE_YEAR] = text.AcceptanceYear;
    headerTexts["postName"] = text.PostName;
    headerTexts["postEmail"] = text.PostEmail;
    headerTexts["__link"] = text.Details;

    return headerTexts;
}

const DisplayMembersTable = ({
    group,
    users,
    noUsersText,
    margin = "0px",
    title
}) => {
    const [text, activeLanguage] = useDigitTranslations(translations);

    return (
        <DigitTable
            margin={margin}
            titleText={title == null ? text.Users : title}
            searchText={text.SearchForUsers}
            idProp={USER_CID}
            startOrderBy={USER_FIRST_NAME}
            columnsOrder={[
                ...[USER_NICK, "postName"],
                ...(group != null && group.active ? ["postEmail"] : [])
            ]}
            headerTexts={generateHeaderTexts(text)}
            data={users.map(user => {
                const officialPostName =
                    activeLanguage === "sv"
                        ? user.post.svText
                        : user.post.enText;

                const unofficialPostName =
                    user.unofficialPostName === officialPostName ||
                    user.unofficialPostName == null ||
                    user.unofficialPostName === ""
                        ? ""
                        : " - " + user.unofficialPostName;

                var postEmail;

                if (group != null && group.active && user.post.emailPrefix) {
                    postEmail = user.post.emailPrefix + "." + group.email;
                }

                return {
                    ...user,
                    postName: officialPostName + unofficialPostName,
                    postEmail,
                    __link: user.id != null ? "/users/" + user.id : null
                };
            })}
            emptyTableText={noUsersText || text.NoUsers}
            startRowsPerPage={
                users.length < 5 ? 5 : users.length < 10 ? 10 : 25
            }
            size={{ minWidth: "288px", height: "100%" }}
        />
    );
};

DisplayMembersTable.defaultProps = {
    users: []
};

export default DisplayMembersTable;
