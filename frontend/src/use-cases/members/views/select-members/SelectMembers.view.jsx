import React from "react";
import {
    DigitButton,
    DigitTable,
    DigitTranslations,
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

const SelectMembers = () => {
    return null;
};

class SelectMembers1 extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            selectedMemberIds: [],
            currentMembers: [],
            currentMemberIds: []
        };
    }

    componentWillUpdate(nextProps, nextState, nextContext) {
        if (
            nextProps.group != null &&
            nextProps.users != null &&
            !nextState.hasLoadedCurrentMembers
        ) {
            const group = nextProps.group;

            var selectedMembers = sessionStorage.getItem(
                group.id + ".selectedMembers"
            );

            if (selectedMembers != null) {
                selectedMembers = JSON.parse(selectedMembers);
            }

            if (selectedMembers == null || selectedMembers.length === 0) {
                selectedMembers = group.groupMembers.map(member => member.id);
            }

            this.setState(
                {
                    currentMembers: group.groupMembers,
                    currentMemberIds: group.groupMembers.map(
                        member => member.id
                    ),
                    selectedMemberIds: selectedMembers,
                    hasLoadedCurrentMembers: true
                },
                () => {
                    this._save(selectedMembers);
                }
            );
        }
    }

    _save = selectedMembers => {
        sessionStorage.setItem(
            this.props.group.id + ".selectedMembers",
            JSON.stringify(selectedMembers)
        );
    };

    _unsavedEdits = () => {
        const { selectedMemberIds, currentMemberIds } = this.state;

        return !_.isEqual(
            _.sortBy(selectedMemberIds),
            _.sortBy(currentMemberIds)
        );
    };

    componentWillUnmount() {
        const { selectedMemberIds } = this.state;

        this._save(selectedMemberIds);
    }

    onSelectedChange = selected => {
        this.setState({
            selectedMemberIds: selected,
            unsavedEdits: selected.length > 0
        });

        this._save(selected);
    };

    generateHeaderTexts = text => {
        const headerTexts = {};

        headerTexts[FIRST_NAME] = text.FirstName;
        headerTexts[LAST_NAME] = text.LastName;
        headerTexts[NICK] = text.Nickname;
        headerTexts[ID] = text.Id;
        headerTexts["__link"] = text.Link;
        headerTexts["__checkbox"] = text.Checkbox;

        return headerTexts;
    };

    render() {
        const [text] = useDigitTranslations(translations);

        const { selectedMemberIds, currentMembers } = this.state;
        const { users, group, onMembersSelected } = this.props;

        const unsavedEdits = this._unsavedEdits();

        if (
            group != null &&
            users != null &&
            (currentMembers.length > 0 ? users.length > 0 : true)
        ) {
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
                                <div>
                                    <DigitButton
                                        disabled={!unsavedEdits}
                                        raised
                                        primary
                                        text={"Nästa"}
                                        onClick={() => {
                                            onMembersSelected(
                                                selectedMemberIds
                                            );
                                        }}
                                    />
                                </div>
                            </DigitLayout.Row>
                        </DigitDesign.CardBody>
                    </DigitDesign.Card>
                    <DigitLayout.Row>
                        <UsersInGroupChanges
                            currentMembers={currentMembers}
                            selectedMembers={selectedMemberIds.map(memberId =>
                                _.find(users, { id: memberId })
                            )}
                        />
                        <DigitLayout.Column>
                            <DigitTable
                                selected={selectedMemberIds}
                                onSelectedUpdated={this.onSelectedChange}
                                search
                                titleText={text.UsersFor + group[PRETTY_NAME]}
                                searchText="Search for users"
                                showSearchableProps
                                idProp="id"
                                startOrderBy={NICK}
                                columnsOrder={[FIRST_NAME, NICK, LAST_NAME]}
                                headerTexts={this.generateHeaderTexts(text)}
                                data={users.map(user => ({
                                    ...user,
                                    __link: "/users/" + user.cid
                                }))}
                            />
                        </DigitLayout.Column>
                    </DigitLayout.Row>
                </DigitLayout.Column>
            );
        } else {
            return null;
        }
    }
}

export default SelectMembers;
