import React from "react";
import { Switch, Route } from "react-router-dom";
import { DigitStepper, DigitTranslations } from "@cthit/react-digit-components";
import SelectMembers from "./views/select-members";
import SetPostNames from "./views/set-post-names";
import ReviewChanges from "./views/review-changes";

import translations from "./EditUsersInGroup.screen.translations";
import { NAME } from "../../../../api/groups/props.groups.api";

class EditUsersInGroup extends React.Component {
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

    _getDataFromSessionStorage = groupId => {
        return JSON.parse(sessionStorage.getItem(groupId + ".selectedMembers"));
    };

    onMembersSelected = (memberIdsSelected, redirectTo, group) => {
        this.setState(
            {
                activeStep: 1,
                memberIdsSelected
            },
            () => {
                redirectTo("/groups/" + group.id + "/members/posts");
            }
        );
    };

    render() {
        const { groupId, group, users, posts, route, redirectTo } = this.props;

        var step = 0; //Ends with members

        if (route.endsWith("/posts")) {
            step = 1;
        } else if (route.endsWith("/review")) {
            step = 2;
        }

        if (
            group != null &&
            users != null &&
            posts != null &&
            users.length > 0
        ) {
            return (
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <>
                            <DigitStepper
                                activeStep={step}
                                steps={[
                                    { text: text.SelectMembers },
                                    { text: text.SetPostNames },
                                    { text: text.ReviewChanges }
                                ]}
                            />

                            <Switch>
                                <Route
                                    path="/groups/:id/members"
                                    exact
                                    render={() => (
                                        <SelectMembers
                                            group={group}
                                            users={users}
                                            groupId={groupId}
                                            onMembersSelected={members =>
                                                this.onMembersSelected(
                                                    members,
                                                    redirectTo,
                                                    group
                                                )
                                            }
                                        />
                                    )}
                                />
                                <Route
                                    path="/groups/:id/members/posts"
                                    exact
                                    render={() => (
                                        <SetPostNames
                                            groupId={groupId}
                                            posts={posts}
                                            currentMembers={group.groupMembers}
                                            users={users}
                                            onNewMembers={() =>
                                                redirectTo(
                                                    "/groups/" +
                                                        group.id +
                                                        "/members/review"
                                                )
                                            }
                                        />
                                    )}
                                />
                                <Route
                                    path="/groups/:id/members/review"
                                    exact
                                    render={() => (
                                        <ReviewChanges
                                            groupName={group[NAME]}
                                            posts={posts}
                                            previousMembers={group.groupMembers}
                                            groupId={groupId}
                                        />
                                    )}
                                />
                            </Switch>
                        </>
                    )}
                />
            );
        } else {
            return null;
        }
    }
}

export default EditUsersInGroup;
