import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useHistory } from "react-router-dom";

import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    useDigitTranslations,
    useDigitToast,
    useDigitDialog,
    DigitRadioButtonGroup,
    DigitLoading
} from "@cthit/react-digit-components";

import {
    deleteAuthority,
    deleteAuthorityLevel,
    deletePostAuthority,
    deleteSuperGroupAuthority,
    deleteUserAuthority
} from "api/authorities/delete.authoritites";
import { getAuthorityLevel } from "api/authorities/get.authorities";
import { getPosts } from "api/posts/get.posts.api";
import { POST_ID } from "api/posts/props.posts.api";
import { getSuperGroups } from "api/super-groups/get.super-groups.api";
import { SG_ID, SG_PRETTY_NAME } from "api/super-groups/props.super-groups.api";

import FiveZeroZero from "../../../../app/elements/five-zero-zero";
import translations from "./EditAuthority.screen.translations";
import AuthoritiesList from "../../authorities-list";
import AddPostAuthority from "./add-post-authority";
import AddSuperGroupAuthority from "./add-supergroup-authority";
import { getUsersMinified } from "../../../../api/users/get.users.api";
import AddUserAuthority from "./add-user-authority";
import FourOFour from "../../../four-o-four";

const EditAuthority = () => {
    const [text, activeLanguage] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const [openDialog] = useDigitDialog();

    const { id } = useParams();
    const history = useHistory();
    const [error, setError] = useState();
    const [authorityLevel, setAuthorityLevel] = useState(null);
    const [superGroupMap, setSuperGroups] = useState(null);
    const [postMap, setPosts] = useState(null);
    const [userOptions, setUsers] = useState(null);
    const [read, setRead] = useState(true);
    const [createType, setCreateType] = useState("superGroup");

    useEffect(() => {
        if (read) {
            getAuthorityLevel(id)
                .then(response => {
                    setAuthorityLevel(response.data);
                })
                .catch(error => {
                    setError(error);
                });
        }
        setRead(false);
    }, [id, read]);

    useEffect(() => {
        if (authorityLevel == null) {
            return;
        }
        Promise.all([getSuperGroups(), getPosts(), getUsersMinified()])
            .then(([superGroupsResponse, postsResponse, usersResponse]) => {
                const superGroups = superGroupsResponse.data;
                const superGroupMap = {};
                for (let i = 0; i < superGroups.length; i++) {
                    superGroupMap[superGroups[i][SG_ID]] =
                        superGroups[i][SG_PRETTY_NAME];
                }

                setSuperGroups(superGroupMap);

                const posts = postsResponse.data;
                const postMap = {};
                for (let i = 0; i < posts.length; i++) {
                    postMap[posts[i][POST_ID]] = posts[i][activeLanguage];
                }

                setPosts(postMap);

                const users = usersResponse.data;
                setUsers(
                    users.map(user => ({
                        value: user.id,
                        text:
                            user.firstName +
                            ' "' +
                            user.nick +
                            '" ' +
                            user.lastName
                    }))
                );
            })
            .catch(error => {
                setError(error);
            });
    }, [authorityLevel, activeLanguage]);

    if (error != null && error.response.status === 500) {
        return <FiveZeroZero reset={() => setError(null)} />;
    }

    if (error != null && error.response.status === 404) {
        return <FourOFour />;
    }

    if (
        authorityLevel == null ||
        superGroupMap == null ||
        postMap == null ||
        userOptions == null
    ) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    const { users, superGroups, posts, authorityLevelName } = authorityLevel;

    return (
        <DigitLayout.Column flex={"1"}>
            <DigitDesign.Card
                size={{ width: "100%", maxWidth: "400px" }}
                alignSelf={"center"}
                margin={{ bottom: "16px" }}
            >
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle
                        text={text.Edit + " " + authorityLevelName}
                    />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    <AuthoritiesList
                        users={users}
                        superGroups={superGroups}
                        posts={posts}
                        itemOnClick={(item, type) => {
                            openDialog({
                                title: text.AreYouSure,
                                description: text.DeleteAuthorityDescription,
                                cancelButtonText: text.Cancel,
                                confirmButtonText: text.Delete,
                                onConfirm: () => {
                                    (type === "user"
                                        ? deleteUserAuthority(
                                              authorityLevelName,
                                              item.id
                                          )
                                        : type === "superGroup"
                                        ? deleteSuperGroupAuthority(
                                              authorityLevelName,
                                              item.id
                                          )
                                        : type === "post"
                                        ? deletePostAuthority(
                                              authorityLevelName,
                                              item.superGroup.id,
                                              item.post.id
                                          )
                                        : new Promise((resolve, reject) =>
                                              reject()
                                          )
                                    )
                                        .then(() => {
                                            setRead(true);
                                            queueToast({
                                                text: text.AuthorityDeleted
                                            });
                                        })
                                        .catch(() => {
                                            queueToast({
                                                text:
                                                    text.FailedAuthorityDeleted
                                            });
                                        });
                                }
                            });
                        }}
                    />
                </DigitDesign.CardBody>
                <DigitDesign.CardButtons leftRight>
                    <DigitButton
                        outlined
                        text={text.Back}
                        onClick={() => history.goBack()}
                    />
                    <DigitButton
                        text={text.Delete}
                        onClick={() => {
                            openDialog({
                                title: text.AreYouSure,
                                description:
                                    text.AreYouSureDeleteAuthorityLevel,
                                confirmButtonText: text.ImSure,
                                cancelButtonText: text.Cancel,
                                onConfirm: () => {
                                    deleteAuthorityLevel(authorityLevelName)
                                        .then(() => {
                                            queueToast({
                                                text: text.DeleteSuccessful
                                            });
                                            forceUpdate();
                                        })
                                        .catch(() => {
                                            queueToast({
                                                text: text.DeleteFailed
                                            });
                                        });
                                }
                            });
                        }}
                    />
                </DigitDesign.CardButtons>
            </DigitDesign.Card>
            <DigitDesign.Card
                size={{ width: "100%", maxWidth: "400px" }}
                alignSelf={"center"}
                margin={{ bottom: "16px" }}
            >
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.TypeOfAuthority} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    <DigitRadioButtonGroup
                        value={createType}
                        onChange={e => setCreateType(e.target.value)}
                        radioButtons={[
                            {
                                id: "superGroup",
                                primary: true,
                                label: text.SuperGroup
                            },
                            {
                                id: "user",
                                primary: true,
                                label: text.User
                            },
                            {
                                id: "post",
                                primary: true,
                                label: text.PostSuperGroup
                            }
                        ]}
                    />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
            {createType === "post" && (
                <AddPostAuthority
                    postMap={postMap}
                    superGroupMap={superGroupMap}
                    setRead={setRead}
                    authorityLevelName={authorityLevelName}
                />
            )}
            {createType === "superGroup" && (
                <AddSuperGroupAuthority
                    superGroupMap={superGroupMap}
                    setRead={setRead}
                    authorityLevelName={authorityLevelName}
                />
            )}
            {createType === "user" && (
                <AddUserAuthority
                    userOptions={userOptions}
                    setRead={setRead}
                    authorityLevelName={authorityLevelName}
                />
            )}
        </DigitLayout.Column>
    );
};

export default EditAuthority;
