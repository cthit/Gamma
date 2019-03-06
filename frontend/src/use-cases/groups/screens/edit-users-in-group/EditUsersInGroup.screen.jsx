import React from "react";

import {
    DigitStepper,
    DigitTranslations,
    DigitComponentSelector
} from "@cthit/react-digit-components";
import SelectMembers from "./views/select-members";
import SetPostNames from "./views/set-post-names";

import translations from "./EditUsersInGroup.screen.translations";
import * as _ from "lodash";

class EditUsersInGroup extends React.Component {
    state = {
        activeStep: 0,
        memberIdsSelected: []
    };

    constructor(props) {
        super(props);

        const {
            loadUsers,
            getGroup,
            groupId,
            gammaLoadingFinished,
            getPosts
        } = this.props;

        Promise.all([loadUsers(), getGroup(groupId), getPosts()]).then(
            result => {
                gammaLoadingFinished();
            }
        );
    }

    render() {
        const { activeStep, memberIdsSelected } = this.state;
        const { groupId, group, users, posts } = this.props;

        return (
            <DigitTranslations
                translations={translations}
                render={text => (
                    <React.Fragment>
                        <DigitStepper
                            activeStep={activeStep}
                            steps={[
                                { text: text.SelectMembers },
                                { text: text.SetPostNames }
                            ]}
                        />
                        <DigitComponentSelector
                            activeComponent={activeStep}
                            components={[
                                () => (
                                    <SelectMembers
                                        group={group}
                                        users={users}
                                        groupId={groupId}
                                        onMembersSelected={memberIdsSelected => {
                                            this.setState({
                                                activeStep: 1,
                                                memberIdsSelected
                                            });
                                        }}
                                    />
                                ),
                                () => (
                                    <SetPostNames
                                        posts={posts}
                                        currentMembers={group.groupMembers}
                                        members={memberIdsSelected.map(
                                            memberId =>
                                                _.find(users, { id: memberId })
                                        )}
                                    />
                                )
                            ]}
                        />
                    </React.Fragment>
                )}
            />
        );
    }
}

export default EditUsersInGroup;
