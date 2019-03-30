import React from "react";
import {
    DigitButton,
    DigitIfElseRendering,
    DigitTable,
    DigitTranslations,
    DigitText,
    DigitLayout,
    DigitDesign
} from "@cthit/react-digit-components";
import translations from "./SelectMembers.view.translations";
import UsersInGroupChanges from "./elements/users-in-group-changes";
import * as _ from "lodash";
import { PRETTY_NAME } from "../../../../../../api/groups/props.groups.api";
import {
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../../../../../api/users/props.users.api";

class SelectMembers extends React.Component {
    constructor(props) {
        super(props);

        const { savedSelectedGroups } = this.props;

        this.state = {
            unsavedEdits:
                savedSelectedGroups != null ||
                (savedSelectedGroups != null &&
                    savedSelectedGroups.length === 0),
            selectedMemberIds:
                savedSelectedGroups == null ? [] : savedSelectedGroups,
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
            console.log("OMGOMGOMGOMG");
            const group = nextProps.group;
            console.log(group);
            this.setState({
                currentMembers: group.groupMembers,
                selectedMemberIds: this.state.selectedMemberIds.concat(
                    group.groupMembers.map(member => member.id)
                ),
                hasLoadedCurrentMembers: true
            });
        }
    }

    componentWillUnmount() {
        const {
            groupId,
            temporarySaveSelectedUsersToGroup,
            gammaLoadingStart
        } = this.props;
        const { selectedMemberIds, currentUsers } = this.state;

        temporarySaveSelectedUsersToGroup(groupId, selectedMemberIds);
    }

    onSelectedChange = selected => {
        this.setState({
            selectedMemberIds: selected,
            unsavedEdits: selected.length > 0
        });
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
        const { selectedMemberIds, unsavedEdits, currentMembers } = this.state;
        const { users, group } = this.props;

        return (
            <DigitIfElseRendering
                test={
                    group != null &&
                    users != null &&
                    (currentMembers.length > 0 ? users.length > 0 : true)
                }
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Column>
                                <DigitDesign.Card>
                                    <DigitDesign.CardBody>
                                        <DigitLayout.Row
                                            justifyContent={"space-between"}
                                        >
                                            <DigitText.Heading5
                                                text={
                                                    unsavedEdits
                                                        ? text.UnsavedEdits
                                                        : text.NoChanges
                                                }
                                            />
                                            <div>
                                                <DigitButton
                                                    raised
                                                    primary
                                                    text={"Nästa"}
                                                    onClick={() => {
                                                        console.log(
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
                                        selectedMembers={selectedMemberIds.map(
                                            memberId =>
                                                _.find(users, { id: memberId })
                                        )}
                                    />
                                    <DigitLayout.Column>
                                        <DigitTable
                                            selected={selectedMemberIds}
                                            onSelectedUpdated={
                                                this.onSelectedChange
                                            }
                                            search
                                            titleText={
                                                text.UsersFor +
                                                group[PRETTY_NAME]
                                            }
                                            searchText="Search for users"
                                            showSearchableProps
                                            idProp="id"
                                            startOrderBy={NICK}
                                            columnsOrder={[
                                                ID,
                                                FIRST_NAME,
                                                NICK,
                                                LAST_NAME
                                            ]}
                                            headerTexts={this.generateHeaderTexts(
                                                text
                                            )}
                                            data={users.map(user => ({
                                                ...user,
                                                __link: "/users/" + user.cid
                                            }))}
                                        />
                                    </DigitLayout.Column>
                                </DigitLayout.Row>
                            </DigitLayout.Column>
                        )}
                    />
                )}
            />
        );
    }
}

export default SelectMembers;
