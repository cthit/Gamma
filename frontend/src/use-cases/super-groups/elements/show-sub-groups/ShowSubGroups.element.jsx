import React from "react";

import { DigitText, useDigitTranslations } from "@cthit/react-digit-components";

import DisplayGroupsTable from "common/elements/display-groups-table/DisplayGroupsTable.element";

import translations from "./ShowSubGroups.element.translations";

const ShowSubGroups = ({ title, subGroups, margin }) => {
    const [text] = useDigitTranslations(translations);
    if (subGroups == null || subGroups.length === 0) {
        return <DigitText.Text text={text.NoSubGroups} />;
    } else {
        return (
            <DisplayGroupsTable
                margin={margin}
                title={title}
                groups={subGroups}
            />
        );
    }
};

export default ShowSubGroups;
