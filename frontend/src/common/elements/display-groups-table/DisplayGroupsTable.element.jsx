import React from "react";
import translations from "./DisplayGroupsTable.element.translations";
import {
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";
import {
    GROUP_DESCRIPTION,
    GROUP_EMAIL,
    GROUP_FUNCTION,
    GROUP_ID,
    GROUP_NAME,
    GROUP_PRETTY_NAME
} from "../../../api/groups/props.groups.api";

function generateHeaderTexts(text) {
    const output = {};

    output[GROUP_ID] = text.Id;
    output[GROUP_NAME] = text.Name;
    output[GROUP_DESCRIPTION] = text.Description;
    output[GROUP_EMAIL] = text.Email;
    output[GROUP_FUNCTION] = text.Function;
    output[GROUP_PRETTY_NAME] = text.PrettyName;
    output["__link"] = text.Details;

    return output;
}

function modifyData(groups, text, activeLanguage, columns) {
    return groups.map(group => {
        const newGroup = { ...group };

        newGroup[GROUP_ID] = group[GROUP_ID];
        newGroup[GROUP_NAME] = group[GROUP_NAME];
        newGroup[GROUP_DESCRIPTION] =
            columns.includes(GROUP_DESCRIPTION) &&
            group[GROUP_DESCRIPTION] != null
                ? group[GROUP_DESCRIPTION][activeLanguage]
                : null;
        newGroup[GROUP_EMAIL] = group[GROUP_EMAIL];
        newGroup[GROUP_FUNCTION] =
            columns.includes(GROUP_FUNCTION) && group[GROUP_FUNCTION] != null
                ? group[GROUP_FUNCTION][activeLanguage]
                : null;
        newGroup["__link"] = "/groups/" + group[GROUP_ID];

        return newGroup;
    });
}

const DisplayGroupsTable = ({ title, groups, columnsOrder, margin = {} }) => {
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
            data={modifyData(groups, text, activeLanguage, columnsOrder)}
            emptyTableText={text.NoGroups}
        />
    );
};

DisplayGroupsTable.defaultProps = {
    columnsOrder: [GROUP_NAME, GROUP_EMAIL]
};

export default DisplayGroupsTable;
