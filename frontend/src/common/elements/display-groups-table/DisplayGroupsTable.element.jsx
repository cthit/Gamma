import React from "react";
import translations from "./DisplayGroupsTable.element.translations";
import {
    DigitTable,
    DigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";
import {
    DESCRIPTION,
    EMAIL,
    FUNCTION,
    ID,
    NAME
} from "../../../api/groups/props.groups.api";

function generateHeaderTexts(text) {
    const output = {};

    output[ID] = text.Id;
    output[NAME] = text.Name;
    output[DESCRIPTION] = text.Description;
    output[EMAIL] = text.Email;
    output[FUNCTION] = text.Function;
    output["__link"] = text.Details;

    return output;
}

function modifyData(groups, text, activeLanguage) {
    return groups.map(group => {
        const newGroup = {};

        newGroup[ID] = group[ID];
        newGroup[NAME] = group[NAME];
        newGroup[DESCRIPTION] = group[DESCRIPTION][activeLanguage];
        newGroup[EMAIL] = group[EMAIL];
        newGroup[FUNCTION] = group[FUNCTION][activeLanguage];
        newGroup["__link"] = "/groups/" + group[ID];

        return newGroup;
    });
}

const DisplayGroupsTable = ({ title, groups }) => (
    <DigitTranslations
        translations={translations}
        render={(text, activeLanguage) => (
            <DigitLayout.Fill>
                <DigitTable
                    titleText={title ? title : text.Groups}
                    searchText={text.SearchForGroups}
                    idProp="id"
                    startOrderBy={NAME}
                    columnsOrder={[ID, NAME, DESCRIPTION, EMAIL, FUNCTION]}
                    headerTexts={generateHeaderTexts(text)}
                    data={modifyData(groups, text, activeLanguage)}
                    emptyTableText={text.NoGroups}
                />
            </DigitLayout.Fill>
        )}
    />
);

export default DisplayGroupsTable;
