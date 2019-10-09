import React from "react";
import {
    DigitLayout,
    DigitLoading,
    DigitStepper,
    DigitTranslations
} from "@cthit/react-digit-components";
import translations from "../groups/screens/edit-users-in-group/EditUsersInGroup.screen.translations";
import { Route, Switch } from "react-router";
import SelectMembers from "../groups/screens/edit-users-in-group/views/select-members";
import SetPostNames from "../groups/screens/edit-users-in-group/views/set-post-names";
import ReviewChanges from "../groups/screens/edit-users-in-group/views/review-changes";
import { NAME } from "../../api/groups/props.groups.api";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";

const Members = ({ groupId, group, users, posts, route, redirectTo }) => {
    const admin = useIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    var step = 0; //Ends with members

    if (route.endsWith("/posts")) {
        step = 1;
    } else if (route.endsWith("/review")) {
        step = 2;
    }

    if (group != null && users != null && posts != null && users.length > 0) {
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
        return (
            <DigitLayout.Center>
                <DigitLoading loading />
            </DigitLayout.Center>
        );
    }
};
export default Members;
