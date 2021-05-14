import React, { useEffect, useState } from "react";

import {
    DigitLoading,
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { getApprovals } from "api/approval/get.approval.api";

import translations from "./MeApprovals.translations.screen";

const MeApprovals = () => {
    const [approvals, setApprovals] = useState(null);
    const [text, activeLanguage] = useDigitTranslations(translations);

    useEffect(() => {
        getApprovals().then(response => {
            setApprovals(
                response.data.map(approval => ({
                    name: approval.name,
                    description: approval.description[activeLanguage]
                }))
            );
        });
    }, [activeLanguage]);

    if (approvals == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    return (
        <DigitTable
            backButton
            emptyTableText={text.NotApproved}
            data={approvals}
            idProp={"name"}
            startOrderBy={"name"}
            headerTexts={{
                name: text.Name,
                description: text.Description
            }}
            columnsOrder={["name", "description"]}
            titleText={text.YourApprovedClients}
            margin={"auto"}
            search
        />
    );
};

export default MeApprovals;
