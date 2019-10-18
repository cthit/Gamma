import React from "react";

import {
    DigitIfElseRendering,
    DigitTranslations,
    DigitText
} from "@cthit/react-digit-components";

import translations from "./ShowSuperGroups.element.translations";
import DisplayGroupsTable from "../../../../common/elements/display-groups-table/DisplayGroupsTable.element";

const ShowSubGroups = ({ title, subGroups }) => (
    <DigitIfElseRendering
        test={subGroups == null || subGroups.length === 0}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                render={text => <DigitText.Text text={text.NoSubGroups} />}
            />
        )}
        elseRender={() => (
            <React.Fragment>
                <DisplayGroupsTable title={title} groups={subGroups} />
            </React.Fragment>
        )}
    />
);

export default ShowSubGroups;
