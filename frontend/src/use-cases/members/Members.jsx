import React, { useEffect, useState } from "react";
import { Route, Switch, useHistory, useLocation } from "react-router-dom";

import {
    DigitStepper,
    DigitLayout,
    useDigitTranslations,
    DigitLoading
} from "@cthit/react-digit-components";

import { getGroup } from "api/groups/get.groups.api";
import { GROUP_NAME } from "api/groups/props.groups.api";
import { getPosts } from "api/posts/get.posts.api";
import { getUsersMinified } from "api/users/get.users.api";

import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import translations from "./Members.translations";
import ReviewChanges from "./views/review-changes";
import SelectMembers from "./views/select-members";
import SetPostNames from "./views/set-post-names";

const Members = () => {
    const history = useHistory();
    const location = useLocation();
    const [text] = useDigitTranslations(translations);
    const groupId = location.pathname.split("/")[2];
    const [selectedMemberIds, setSelectedMemberIds] = useState(null);
    const [newMembersData, setNewMembersData] = useState(null);
    const [group, setGroup] = useState(null);
    const [users, setUsers] = useState(null);
    const [posts, setPosts] = useState(null);

    const admin = useGammaIsAdmin();

    useEffect(() => {
        if (admin) {
            Promise.all([
                getPosts(),
                getUsersMinified(),
                getGroup(groupId)
            ]).then(response => {
                if (response.length === 3) {
                    setPosts(response[0].data);
                    setUsers(response[1].data);
                    setGroup(response[2].data);
                }
            });
        }
    }, [admin, groupId]);

    const route = location.pathname;
    const step = route.endsWith("/posts")
        ? 1
        : route.endsWith("/review")
        ? 2
        : 0; //Ends with members

    useEffect(() => {
        //turn into effect instead
        if (step > 0 && selectedMemberIds == null) {
            history.push("/members/" + groupId);
        }
    }, [step, selectedMemberIds, groupId, history]);

    const onMembersSelected = selectedMembers => {
        setSelectedMemberIds(selectedMembers);
        history.push("/members/" + groupId + "/posts");
    };

    if (!admin) {
        return <InsufficientAccess />;
    }

    if (group == null || users == null || posts == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    return (
        <DigitLayout.Column flex={"1"}>
            <DigitStepper
                size={{ minHeight: "min-content" }}
                padding={"0"}
                activeStep={step}
                steps={[
                    { text: text.SelectMembers },
                    { text: text.SetPostNames },
                    { text: text.ReviewChanges }
                ]}
            />

            <Switch>
                <Route
                    path="/members/:id"
                    exact
                    render={() => (
                        <SelectMembers
                            group={group}
                            users={users}
                            groupId={groupId}
                            onMembersSelected={onMembersSelected}
                        />
                    )}
                />
                <Route
                    path="/members/:id/posts"
                    exact
                    render={() => (
                        <SetPostNames
                            selectedMemberIds={selectedMemberIds}
                            groupId={groupId}
                            posts={posts}
                            currentMembers={group.groupMembers}
                            users={users}
                            onNewMembers={value => {
                                setNewMembersData(value.members);
                                history.push("/members/" + groupId + "/review");
                            }}
                        />
                    )}
                />
                <Route
                    path="/members/:id/review"
                    exact
                    render={() => (
                        <ReviewChanges
                            groupName={group[GROUP_NAME]}
                            posts={posts}
                            previousMembers={group.groupMembers}
                            newMembersData={newMembersData}
                            groupId={groupId}
                            onFinished={() => {
                                history.push("/groups");
                                history.push("/groups/" + group.id);
                            }}
                        />
                    )}
                />
            </Switch>
        </DigitLayout.Column>
    );
};
export default Members;
