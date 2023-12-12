import React, { useMemo } from "react";
import {
    DigitText,
    DigitLayout,
    useDigitTranslations
} from "@cthit/react-digit-components";
import translations from "./MeGroups.screen.translations";
import DisplayGroupsTable from "../../../../common/elements/display-groups-table/DisplayGroupsTable.element";
import useGammaUser from "../../../../common/hooks/use-gamma-user/useGammaUser";

const MeGroups = () => {
    const [text] = useDigitTranslations(translations);
    const user = useGammaUser();

    const [activeGroups, pastGroups, futureGroups] = useMemo(() => {
        const now = new Date().getTime();

        return user == null
            ? [[], [], []]
            : [
                  user.groups.filter(g => g.active),
                  user.groups.filter(g => !g.active && g.becomesActive <= now),
                  user.groups.filter(g => !g.active && g.becomesActive > now)
              ];
    }, [user]);

    if (user == null) {
        return null;
    }

    if (
        activeGroups.length === 0 &&
        pastGroups.length === 0 &&
        futureGroups.length === 0
    ) {
        return (
            <DigitLayout.Center>
                <DigitText.Heading3 text={text.NoGroupsForYou} />
            </DigitLayout.Center>
        );
    }

    return (
        <DigitLayout.Column alignItems={"center"} flex={"1"}>
            {futureGroups.length > 0 && (
                <DisplayGroupsTable
                    title={text.FutureGroups}
                    groups={activeGroups}
                />
            )}
            {activeGroups.length > 0 && (
                <DisplayGroupsTable
                    margin={futureGroups.length > 0 ? { top: "16px" } : {}}
                    title={text.ActiveGroups}
                    groups={activeGroups}
                />
            )}
            {pastGroups.length > 0 && (
                <DisplayGroupsTable
                    margin={
                        activeGroups.length > 0 || activeGroups.length > 0
                            ? { top: "16px" }
                            : {}
                    }
                    title={text.PastGroups}
                    groups={pastGroups}
                />
            )}
        </DigitLayout.Column>
    );
};

export default MeGroups;
