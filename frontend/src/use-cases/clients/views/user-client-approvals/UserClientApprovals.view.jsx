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
    const [users, setUsers] = useState(null);

    const clientId = client.clientId;

    useEffect(() => {
        getAllApprovalsbyClientId(clientId).then(response => {
            setUsers(response.data);
        });
    }, [clientId]);

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
                bottom: "calc(56px + 16px)"
            }}
            search
            searchText={text.Search}
        />
    );
};

export default UserClientApprovals;
