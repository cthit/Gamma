import React, { useMemo } from "react";

import {
    DigitText,
    DigitLayout,
    useDigitTranslations
} from "@cthit/react-digit-components";

import DisplayGroupsTable from "common/elements/display-groups-table/DisplayGroupsTable.element";
import useGammaUser from "common/hooks/use-gamma-user/useGammaUser";

import translations from "./MeGroups.screen.translations";

const MeGroups = () => {
    const [text] = useDigitTranslations(translations);
    const user = useGammaUser();

    if (user == null) {
        return null;
    }

    if (
        user.groups.length === 0
    ) {
        return (
            <DigitLayout.Center>
                <DigitText.Heading3 text={text.NoGroupsForYou}/>
            </DigitLayout.Center>
        );
    }

    return (
        <DigitLayout.Center>
            {user.groups.length > 0 && (
                <DisplayGroupsTable
                    title={text.YourGroups}
                    groups={user.groups.map(membership => membership.group)}
                />
            )}
        </DigitLayout.Center>
    );
};

export default MeGroups;
