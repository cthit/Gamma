import * as _ from "lodash";
import React, { useMemo, useState } from "react";
import styled from "styled-components";

import {
    DigitButton,
    DigitSelectMultipleTable,
    DigitText,
    DigitLayout,
    DigitDesign,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { GROUP_PRETTY_NAME } from "api/groups/props.groups.api";
import {
    USER_FIRST_NAME,
    USER_ID,
    USER_LAST_NAME,
    USER_NICK
} from "api/users/props.users.api";

import translations from "./SelectMembers.view.translations";
import UsersInGroupChanges from "./elements/users-in-group-changes";

const Table = styled.div`
    display: grid;
    grid-template-columns: min-content auto;
    grid-auto-rows: min-content;
    grid-gap: 1rem;

    @media (max-width: 600px) {
        grid-template-columns: auto;
    }
`;

const SpannedCard = styled(DigitDesign.Card)`
    @media (min-width: 600px) {
        grid-column-start: 1;
        grid-column-end: 3;
    }
`;

const generateHeaderTexts = text => {
    const headerTexts = {};

    headerTexts[USER_FIRST_NAME] = text.FirstName;
    headerTexts[USER_LAST_NAME] = text.LastName;
    headerTexts[USER_NICK] = text.Nickname;
    headerTexts[USER_ID] = text.Id;

    return headerTexts;
};

const SelectMembers = ({ users, group, onMembersSelected }) => {
    const [text] = useDigitTranslations(translations);
    const [selectedMemberIds, setSelectedMemberIds] = useState(
        group.members.map(member => member.user.id)
    );

    const unsavedEdits = useMemo(
        () => selectedMemberIds.length !== group.members.length,
        [selectedMemberIds, group.members]
    );

    return (
        <Table>
            <SpannedCard size={{ minHeight: "min-content" }}>
                <DigitDesign.CardBody>
                    <DigitLayout.Row
                        justifyContent={"space-between"}
                        alignItems={"center"}
                    >
                        <DigitText.Heading5
                            text={
                                unsavedEdits
                                    ? text.UnsavedEdits
                                    : text.NoChanges
                            }
                        />
                        <DigitButton
                            raised
                            primary
                            text={text.Next}
                            onClick={() => {
                                onMembersSelected(selectedMemberIds);
                            }}
                        />
                    </DigitLayout.Row>
                </DigitDesign.CardBody>
            </SpannedCard>
            <UsersInGroupChanges
                currentMembers={group.members}
                selectedMembers={selectedMemberIds.map(memberId =>
                    _.find(users, { id: memberId })
                )}
            />
            <DigitSelectMultipleTable
                size={{ minWidth: "0" }}
                margin={"0"}
                disableSelectAll
                flex={"2"}
                value={selectedMemberIds}
                onChange={newSelected => {
                    setSelectedMemberIds(newSelected);
                }}
                search
                titleText={text.UsersFor + group[GROUP_PRETTY_NAME]}
                searchText={text.Search}
                idProp="id"
                startOrderBy={USER_NICK}
                columnsOrder={[USER_FIRST_NAME, USER_NICK, USER_LAST_NAME]}
                headerTexts={generateHeaderTexts(text)}
                data={users}
            />
        </Table>
    );
};

export default SelectMembers;
