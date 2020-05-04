import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getAuthorityLevel } from "../../../../api/authorities/get.authorities";
import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitEditDataCard,
    useDigitTranslations,
    DigitSelect,
    useDigitToast,
    DigitText,
    DigitList,
    useDigitDialog
} from "@cthit/react-digit-components";
import translations from "./EditAuthority.screen.translations";
import { useHistory } from "react-router-dom";
import { getSuperGroups } from "../../../../api/super-groups/get.super-groups.api";
import { getPosts } from "../../../../api/posts/get.posts.api";
import {
    SG_ID,
    SG_PRETTY_NAME
} from "../../../../api/super-groups/props.super-groups.api";
import { POST_ID } from "../../../../api/posts/props.posts.api";
import { addToAuthorityLevel } from "../../../../api/authorities/post.authoritites";
import FiveZeroZero from "../../../../app/elements/five-zero-zero";
import * as yup from "yup";
import DeleteIcon from "@material-ui/icons/Delete";
import { deleteAuthority } from "../../../../api/authorities/delete.authoritites";

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
    const [read, setRead] = useState(true);

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
        Promise.all([getSuperGroups(), getPosts()])
            .then(([superGroupsResponse, postsResponse]) => {
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
            })
            .catch(error => {
                setError(error);
            });
    }, [activeLanguage]);

    if (error != null && error.response.status === 500) {
        return <FiveZeroZero reset={() => setError(null)} />;
    }

    if (authorityLevel == null || superGroupMap == null || postMap == null) {
        return null;
    }

    return (
        <DigitLayout.Column flex={"1"}>
            <DigitDesign.Card
                size={{ width: "100%", maxWidth: "400px" }}
                alignSelf={"center"}
                margin={{ bottom: "16px" }}
            >
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle
                        text={text.Edit + " " + authorityLevel.authorityLevel}
                    />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    {authorityLevel.authorities.length === 0 && (
                        <DigitText.Text alignCenter text={text.NoAuthorities} />
                    )}
                    {authorityLevel.authorities.length > 0 && (
                        <DigitList
                            items={authorityLevel.authorities.map(
                                authority => ({
                                    text:
                                        authority.superGroup[SG_PRETTY_NAME] +
                                        " - " +
                                        authority.post[activeLanguage],
                                    actionIcon: DeleteIcon,
                                    actionOnClick: () => {
                                        openDialog({
                                            title: text.AreYouSure,
                                            description:
                                                text.DeleteAuthorityDescription,
                                            cancelButtonText: text.Cancel,
                                            confirmButtonText: text.Delete,
                                            onConfirm: () => {
                                                deleteAuthority(authority.id)
                                                    .then(() => {
                                                        setRead(true);
                                                        queueToast({
                                                            text:
                                                                text.AuthorityDeleted
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
                                    }
                                })
                            )}
                            onClick={null}
                            dense
                        />
                    )}
                </DigitDesign.CardBody>
                <DigitDesign.CardButtons leftRight>
                    <DigitButton
                        outlined
                        text={text.Back}
                        onClick={() => history.goBack()}
                    />
                </DigitDesign.CardButtons>
            </DigitDesign.Card>
            <DigitEditDataCard
                margin={"0px"}
                size={{ width: "100%", maxWidth: "400px" }}
                alignSelf={"center"}
                centerFields
                submitText={text.Add}
                titleText={text.AddToAuthorityLevel}
                validationSchema={yup.object().shape({
                    superGroup: yup
                        .string()
                        .required(text.SuperGroup + text.IsRequired),
                    post: yup.string().required(text.Post + text.IsRequired)
                })}
                onSubmit={(values, actions) =>
                    addToAuthorityLevel({
                        post: values.post,
                        superGroup: values.superGroup,
                        authority: id
                    })
                        .then(() => {
                            setRead(true);
                            actions.resetForm();
                            queueToast({
                                text: text.AddedToAuthorityLevel
                            });
                        })
                        .catch(() => {
                            queueToast({
                                text: text.FailedToAuthorityLevel
                            });
                        })
                }
                keysOrder={["superGroup", "post"]}
                keysComponentData={{
                    superGroup: {
                        component: DigitSelect,
                        componentProps: {
                            upperLabel: text.SuperGroup,
                            valueToTextMap: superGroupMap,
                            outlined: true
                        }
                    },
                    post: {
                        component: DigitSelect,
                        componentProps: {
                            upperLabel: text.Post,
                            valueToTextMap: postMap,
                            outlined: true
                        }
                    }
                }}
            />
        </DigitLayout.Column>
    );
};

export default EditAuthority;
