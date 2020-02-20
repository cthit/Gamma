import React, { useMemo } from "react";
import {
    DigitText,
    DigitLayout,
    useDigitTranslations,
    useGammaUser
} from "@cthit/react-digit-components";
import translations from "./MeGroups.screen.translations";
import DisplayGroupsTable from "../../../../common/elements/display-groups-table/DisplayGroupsTable.element";

const MeGroups = ({}) => {
    const [text] = useDigitTranslations(translations);
    const user = useGammaUser();

    const [activeGroups, pastGroups] = useMemo(
        () =>
            user == null
                ? [[], []]
                : [
                      user.groups.filter(group => group.active),
                      user.groups.filter(group => !group.active)
                  ],
        [JSON.stringify(user)]
    );

    if (user == null) {
        return null;
    }

    if (activeGroups.length === 0 && pastGroups.length === 0) {
        return (
            <DigitLayout.Center>
                <DigitText.Heading3 text={text.NoGroupsForYou} />
            </DigitLayout.Center>
        );
    }

    console.log(activeGroups);
    console.log(pastGroups);

    return (
        <>
            {activeGroups.length > 0 && (
                <DisplayGroupsTable
                    title={text.ActiveGroups}
                    groups={activeGroups}
                />
            )}
            {pastGroups.length > 0 && (
                <DisplayGroupsTable
                    title={text.PastGroups}
                    groups={pastGroups}
                />
            )}
        </>
    );
};

export default MeGroups;
