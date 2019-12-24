import React, { useEffect, useState } from "react";
import {
    DigitLayout,
    DigitLoading,
    DigitStepper,
    DigitTranslations
} from "@cthit/react-digit-components";
import { Route, Switch } from "react-router";
import { NAME } from "../../api/groups/props.groups.api";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";
import translations from "./Members.translations";
import SetPostNames from "./views/set-post-names";
import ReviewChanges from "./views/review-changes";
import SelectMembers from "./views/select-members";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { getPosts } from "../../api/posts/get.posts.api";
import { getUsersMinified } from "../../api/users/get.users.api";
import { getGroup } from "../../api/groups/get.groups.api";

const Members = ({ history }) => {
    const groupId = history.location.pathname.split("/")[2];
    const [data, setData] = useState({
        group: null,
        users: null,
        posts: null
    });
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, [dispatch]);

    const admin = useIsAdmin();

    useEffect(() => {
        if (admin) {
            Promise.all([
                getPosts(),
                getUsersMinified(),
                getGroup(groupId)
            ]).then(response => {
                setData({
                    posts: response[0].data,
                    users: response[1].data,
                    group: response[2].data
                });
            });
        }
    }, [admin, groupId]);

    if (!admin) {
        return <InsufficientAccess />;
    }

    const redirectTo = to => history.push(to);

    const { group, users, posts } = data;

    var step = 0; //Ends with members

    const route = history.location.pathname;

    const onMembersSelected = () => {
        redirectTo("/members/" + group.id + "/posts");
    };

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
                                        groupId={groupId}
                                        posts={posts}
                                        currentMembers={group.groupMembers}
                                        users={users}
                                        onNewMembers={() =>
                                            redirectTo(
                                                "/members/" +
                                                    groupId +
                                                    "/review"
                                            )
                                        }
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
                                        groupId={groupId}
                                        onFinished={() => {
                                            redirectTo("/groups/" + group.id);
                                        }}
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
