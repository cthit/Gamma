import React, { useEffect, useState } from "react";

import {
    DigitLoading,
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { getAllApprovalsbyClientId } from "api/approval/get.approval.api";

import translations from "./UserClientApprovals.translations.view";

const UserClientApprovals = ({ client }) => {
    const [text] = useDigitTranslations(translations);
    const users = client.approvedUsers;

    if (users == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    return (
        <DigitTable
            startOrderBy={"firstName"}
            idProp={"id"}
            data={users.map(({ firstName, nick, lastName, id }) => ({
                firstName,
                nick,
                lastName,
                id,
                __link: "/users/" + id
            }))}
            headerTexts={{
                firstName: text.FirstName,
                nick: text.Nick,
                lastName: text.LastName,
                __link: text.Details
            }}
            columnsOrder={["firstName", "nick", "lastName"]}
            titleText={text.UsersThatApproved + client.prettyName}
            margin={{
                top: "32px"
            }}
            search
            searchText={text.Search}
        />
    );
};

export default UserClientApprovals;
