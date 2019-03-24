import React from "react";
import { Switch, Route } from "react-router-dom";
import {
    DigitStepper,
    DigitTranslations,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
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
        var hasNew = false;
        for (var i = 0; i < memberIdsSelected.length - 1; i++) {
            const selectedMemberId = memberIdsSelected[i];
            if (groupMemberIds.indexOf(selectedMemberId) === -1) {
                hasNew = true;
                break;
            }
        }

        return hasNew;
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

        return (
            <DigitIfElseRendering
                test={group != null && users != null && users.length > 0}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <React.Fragment>
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
                                                onMembersSelected={memberIdsSelected => {
                                                    this.setState(
                                                        {
                                                            activeStep: 1,
                                                            memberIdsSelected
                                                        },
                                                        () => {
                                                            sessionStorage.setItem(
                                                                group.id +
                                                                    ".selectedMembers",
                                                                JSON.stringify(
                                                                    memberIdsSelected
                                                                )
                                                            );

                                                            if (
                                                                this._hasNewMembers(
                                                                    memberIdsSelected,
                                                                    group
                                                                )
                                                            ) {
                                                                redirectTo(
                                                                    "/groups/" +
                                                                        group.id +
                                                                        "/members/posts"
                                                                );
                                                            } else {
                                                                redirectTo(
                                                                    "/groups/" +
                                                                        group.id +
                                                                        "/members/review"
                                                                );
                                                            }
                                                        }
                                                    );
                                                }}
                                            />
                                        )}
                                    />
                                    <Route
                                        path="/groups/:id/members/posts"
                                        exact
                                        render={() => (
                                            <SetPostNames
                                                posts={posts}
                                                currentMembers={
                                                    group.groupMembers
                                                }
                                                members={memberIdsSelected.map(
                                                    memberId =>
                                                        _.find(users, {
                                                            id: memberId
                                                        })
                                                )}
                                            />
                                        )}
                                    />
                                    <Route
                                        path="/groups/:id/members/review"
                                        exact
                                        render={() => <ReviewChanges />}
                                    />
                                </Switch>
                            </React.Fragment>
                        )}
                    />
                )}
            />
        );
    }
}

export default EditUsersInGroup;
