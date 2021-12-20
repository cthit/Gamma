import React from "react";

import {
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";

import {
    GROUP_ID,
    GROUP_NAME,
    GROUP_PRETTY_NAME
} from "api/groups/props.groups.api";

import translations from "./DisplayGroupsTable.element.translations";

function generateHeaderTexts(text) {
    const output = {};

    output[GROUP_ID] = text.Id;
    output[GROUP_NAME] = text.Name;
    output[GROUP_PRETTY_NAME] = text.PrettyName;
    output["__link"] = text.Details;

    return output;
}

function modifyData(groups, superGroup) {
    return groups.map(group => {
        const newGroup = { ...group };

        newGroup[GROUP_ID] = group[GROUP_ID];
        newGroup[GROUP_NAME] = group[GROUP_NAME];
        newGroup["__link"] =
            (superGroup ? "/super-groups/" : "/groups/") + group[GROUP_ID];

        return newGroup;
    });
}

const DisplayGroupsTable = ({
    title,
    groups,
    columnsOrder,
    margin = {},
    superGroup,
    backButton
}) => {
    const [text, activeLanguage] = useDigitTranslations(translations);

    if (groups == null) {
        return null;
    }

    return (
        <DigitTable
            size={{ width: "100%" }}
            margin={margin}
            titleText={title ? title : text.Groups}
            searchText={text.SearchForGroups}
            idProp="id"
            startOrderBy={GROUP_NAME}
            columnsOrder={columnsOrder}
            headerTexts={generateHeaderTexts(text)}
            data={modifyData(groups, superGroup)}
            emptyTableText={text.NoGroups}
            backButton={backButton}
        />
    );
};

DisplayGroupsTable.defaultProps = {
    columnsOrder: [GROUP_NAME]
};

export default DisplayGroupsTable;
