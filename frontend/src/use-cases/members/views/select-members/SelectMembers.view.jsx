import React, { useMemo, useState } from "react";
import {
    DigitButton,
    DigitTable,
    DigitText,
    DigitLayout,
    DigitDesign,
    useDigitTranslations
} from "@cthit/react-digit-components";
import translations from "./SelectMembers.view.translations";
import UsersInGroupChanges from "./elements/users-in-group-changes";
import * as _ from "lodash";
import { PRETTY_NAME } from "../../../../api/groups/props.groups.api";
import {
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../../../api/users/props.users.api";

const generateHeaderTexts = text => {
    const headerTexts = {};

    headerTexts[FIRST_NAME] = text.FirstName;
    headerTexts[LAST_NAME] = text.LastName;
    headerTexts[NICK] = text.Nickname;
    headerTexts[ID] = text.Id;
    headerTexts["__link"] = text.Link;
    headerTexts["__checkbox"] = text.Checkbox;

    return headerTexts;
};

const SelectMembers = ({ users, group, onMembersSelected }) => {
    const [text] = useDigitTranslations(translations);
    const [selectedMemberIds, setSelectedMemberIds] = useState(
        group.groupMembers.map(member => member.id)
    );

    const unsavedEdits = useMemo(
        () => selectedMemberIds.length !== group.groupMembers.length,
        [selectedMemberIds, group.groupMembers]
    );

    return (
        <DigitLayout.Column>
            <DigitDesign.Card>
                <DigitDesign.CardBody>
                    <DigitLayout.Row justifyContent={"space-between"}>
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
            </DigitDesign.Card>
            <DigitLayout.Row>
                <UsersInGroupChanges
                    currentMembers={group.groupMembers}
                    selectedMembers={selectedMemberIds.map(memberId =>
                        _.find(users, { id: memberId })
                    )}
                />
                <DigitTable
                    selected={selectedMemberIds}
                    onSelectedUpdated={newSelected => {
                        setSelectedMemberIds(newSelected);
                    }}
                    search
                    titleText={text.UsersFor + group[PRETTY_NAME]}
                    searchText="Search for users"
                    idProp="id"
                    startOrderBy={NICK}
                    columnsOrder={[FIRST_NAME, NICK, LAST_NAME]}
                    headerTexts={generateHeaderTexts(text)}
                    data={users.map(user => ({
                        ...user,
                        __link: "/users/" + user.cid
                    }))}
                />
            </DigitLayout.Row>
        </DigitLayout.Column>
    );
};

export default SelectMembers;
