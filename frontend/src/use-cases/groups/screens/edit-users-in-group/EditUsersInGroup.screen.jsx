import React from "react";

import {
    DigitTable,
    DigitTranslations,
    DigitIfElseRendering
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

        this.state = {
            selectedUsers: []
        };

        props.loadUsers();
    }

    onSelectedChange = selected => {
        this.setState({
            selectedUsers: selected
        });
    };

    generateHeaderTexts = text => {
        const headerTexts = {};

        headerTexts[FIRST_NAME] = text.FirstName;
        headerTexts[LAST_NAME] = text.LastName;
        headerTexts[ID] = text.Id;
        headerTexts["__link"] = text.Link;
        headerTexts["__checkbox"] = text.Checkbox;

        return headerTexts;
    };

    render() {
        const { selectedUsers } = this.state;
        const { users, group } = this.props;

        console.log(group);

        return (
            <DigitIfElseRendering
                test={group != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={EditUsersInGroupTranslations}
                        uniquePath="Groups.EditUsersInGroup"
                        render={text => (
                            <DigitTable
                                selected={selectedUsers}
                                onSelectedUpdated={this.onSelectedChange}
                                search
                                titleText={text.UsersFor + group[PRETTY_NAME]}
                                searchText="Search for users"
                                showSearchableProps
                                idProp="id"
                                startOrderBy={NICKNAME}
                                columnsOrder={[ID, FIRST_NAME, LAST_NAME]}
                                headerTexts={this.generateHeaderTexts(text)}
                                data={users.map(user => ({
                                    ...user,
                                    __link: "/users/" + user.cid
                                }))}
                            />
                        )}
                    />
                )}
            />
        );
    }
}

export default EditUsersInGroup;
