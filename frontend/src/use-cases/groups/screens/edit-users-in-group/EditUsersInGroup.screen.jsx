import React from "react";

import {
    DigitTable,
    DigitTranslations,
    DigitIfElseRendering,
    DigitLayout,
    DigitText
} from "@cthit/react-digit-components";

import {
    FIRST_NAME,
    LAST_NAME,
    NICKNAME,
    ID
} from "../../../../api/users/props.users.api";

import { PRETTY_NAME } from "../../../../api/groups/props.groups.api";

import EditUsersInGroupTranslations from "./EditUsersInGroup.screen.translations";

class EditUsersInGroup extends React.Component {
    constructor(props) {
        super(props);

        const { savedSelectedGroups } = this.props;

        this.state = {
            unsavedEdits:
                savedSelectedGroups != null ||
                (savedSelectedGroups != null &&
                    savedSelectedGroups.length === 0),
            selectedUsers:
                savedSelectedGroups == null ? [] : savedSelectedGroups
        };

        props.loadUsers();
    }

    componentDidMount() {
        const {
            loadUsers,
            getGroup,
            groupId,
            gammaLoadingFinished
        } = this.props;

        Promise.all([loadUsers(), getGroup(groupId)]).then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        const {
            groupId,
            temporarySaveSelectedUsersToGroup,
            gammaLoadingStart
        } = this.props;
        const { selectedUsers } = this.state;

        temporarySaveSelectedUsersToGroup(groupId, selectedUsers);

        gammaLoadingStart();
    }

    onSelectedChange = selected => {
        this.setState({
            selectedUsers: selected,
            unsavedEdits: selected.length > 0
        });
    };

    generateHeaderTexts = text => {
        const headerTexts = {};

        headerTexts[FIRST_NAME] = text.FirstName;
        headerTexts[LAST_NAME] = text.LastName;
        headerTexts[NICKNAME] = text.Nickname;
        headerTexts[ID] = text.Id;
        headerTexts["__link"] = text.Link;
        headerTexts["__checkbox"] = text.Checkbox;

        return headerTexts;
    };

    render() {
        const { selectedUsers, unsavedEdits } = this.state;
        const { users, group } = this.props;

        return (
            <DigitIfElseRendering
                test={group != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={EditUsersInGroupTranslations}
                        uniquePath="Groups.EditUsersInGroup"
                        render={text => (
                            <DigitLayout.Column>
                                <DigitText.Heading5
                                    text={unsavedEdits ? text.UnsavedEdits : ""}
                                />
                                <DigitTable
                                    selected={selectedUsers}
                                    onSelectedUpdated={this.onSelectedChange}
                                    search
                                    titleText={
                                        text.UsersFor + group[PRETTY_NAME]
                                    }
                                    searchText="Search for users"
                                    showSearchableProps
                                    idProp="id"
                                    startOrderBy={NICKNAME}
                                    columnsOrder={[
                                        ID,
                                        FIRST_NAME,
                                        NICKNAME,
                                        LAST_NAME
                                    ]}
                                    headerTexts={this.generateHeaderTexts(text)}
                                    data={users.map(user => ({
                                        ...user,
                                        __link: "/users/" + user.cid
                                    }))}
                                />
                            </DigitLayout.Column>
                        )}
                    />
                )}
            />
        );
    }
}

export default EditUsersInGroup;
