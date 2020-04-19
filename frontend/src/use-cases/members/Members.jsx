import React, { useEffect, useState } from "react";
import {
    DigitStepper,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { Route, Switch, useHistory, useLocation } from "react-router";
import { NAME } from "../../api/groups/props.groups.api";
import InsufficientAccess from "../../common/views/insufficient-access";
import translations from "./Members.translations";
import SetPostNames from "./views/set-post-names";
import ReviewChanges from "./views/review-changes";
import SelectMembers from "./views/select-members";
import { getPosts } from "../../api/posts/get.posts.api";
import { getUsersMinified } from "../../api/users/get.users.api";
import { getGroup } from "../../api/groups/get.groups.api";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";

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

    if (!admin) {
        return <InsufficientAccess />;
    }

    var step = 0; //Ends with members

    const route = location.pathname;

    const onMembersSelected = selectedMembers => {
        setSelectedMemberIds(selectedMembers);
        history.push("/members/" + groupId + "/posts");
    };

    if (route.endsWith("/posts")) {
        step = 1;
    } else if (route.endsWith("/review")) {
        step = 2;
    }

    if (group == null || users == null || posts == null) {
        return null;
    }

    //turn into effect instead
    if (step > 0 && selectedMemberIds == null) {
        history.push("/members/" + groupId);
        return null;
    }

    return (
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
                            groupName={group[NAME]}
                            posts={posts}
                            previousMembers={group.groupMembers}
                            newMembersData={newMembersData}
                            groupId={groupId}
                            onFinished={() => {
                                history.push("/groups/" + group.id);
                            }}
                        />
                    )}
                />
            </Switch>
        </>
    );
};
export default Members;
