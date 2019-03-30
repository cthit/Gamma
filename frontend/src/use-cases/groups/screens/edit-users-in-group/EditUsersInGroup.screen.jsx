import React from "react";
import { Switch, Route } from "react-router-dom";
import { DigitStepper, DigitTranslations } from "@cthit/react-digit-components";
import SelectMembers from "./views/select-members";
import SetPostNames from "./views/set-post-names";
import ReviewChanges from "./views/review-changes";

import translations from "./EditUsersInGroup.screen.translations";
import * as _ from "lodash";

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

        var memberIdsSelected = this._getDataFromSessionStorage(groupId);
        if (memberIdsSelected == null) {
            memberIdsSelected = [];
        }

        this.state = {
            memberIdsSelected: memberIdsSelected
        };
    }

    _getDataFromSessionStorage = groupId => {
        return JSON.parse(sessionStorage.getItem(groupId + ".selectedMembers"));
    };

    _hasNewMembers = (memberIdsSelected, group) => {
        const groupMemberIds = group.groupMembers.filter(
            groupMember => groupMember.id
        );
        console.log(groupMemberIds);
        var hasNew = false;
        for (var i = 0; i < memberIdsSelected.length - 1; i++) {
            const selectedMemberId = memberIdsSelected[i];
            if (memberIdsSelected.indexOf(selectedMemberId) === -1) {
                hasNew = true;
                break;
            }
        }

        return memberIdsSelected
            .filter(
                memberIdSelected =>
                    groupMemberIds.indexOf(memberIdSelected) === -1
            )
            .reduce((accumulator, value) => accumulator || value);
    };

    onMembersSelected = (memberIdsSelected, redirectTo, group) => {
        this.setState(
            {
                activeStep: 1,
                memberIdsSelected
            },
            () => {
                if (this._hasNewMembers(memberIdsSelected, group)) {
                    redirectTo("/groups/" + group.id + "/members/posts");
                } else {
                    redirectTo("/groups/" + group.id + "/members/review");
                }
            }
        );
    };

    render() {
        const { memberIdsSelected } = this.state;
        const { groupId, group, users, posts, route, redirectTo } = this.props;

        var step = 0; //Ends with members

        if (route.endsWith("/posts")) {
            step = 1;
        } else if (route.endsWith("/review")) {
            step = 2;
        }

        if (group != null && users != null && users.length > 0) {
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
                                            members={memberIdsSelected
                                                .filter(
                                                    member =>
                                                        memberIdsSelected.indexOf(
                                                            member.id
                                                        ) === -1
                                                )
                                                .map(memberId =>
                                                    _.find(users, {
                                                        id: memberId
                                                    })
                                                )}
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
                                        <ReviewChanges groupId={groupId} />
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
