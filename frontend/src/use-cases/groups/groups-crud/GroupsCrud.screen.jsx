import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import {
    DigitCRUD,
    useDigitTranslations,
    DigitButton,
    DigitLayout,
    DigitLoading
} from "@cthit/react-digit-components";

import { deleteGroup } from "api/groups/delete.groups.api";
import { getGroup, getGroupsMinified } from "api/groups/get.groups.api";
import { addGroup } from "api/groups/post.groups.api";
import {
    GROUP_MEMBERS,
    GROUP_ID,
    GROUP_NAME,
    GROUP_PRETTY_NAME,
    GROUP_SUPER_GROUP,
    GROUP_EMAIL
} from "api/groups/props.groups.api";
import { editGroup } from "api/groups/put.groups.api";
import { getSuperGroups } from "api/super-groups/get.super-groups.api";

import DisplayMembersTable from "common/elements/display-members-table";
import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import FiveZeroZero from "../../../app/elements/five-zero-zero";
import FourOFour from "../../four-o-four";
import {
    initialValues,
    keysOrder,
    keysText,
    readAllKeysOrder,
    readOneKeysOrder,
    keysComponentData,
    validationSchema,
    updateKeysOrder
} from "./GroupsCrud.screen.options";
import translations from "./GroupsCrud.screen.translations";

const GroupsCrud = () => {
    const [text] = useDigitTranslations(translations);
    const admin = useGammaIsAdmin();
    const [superGroups, setSuperGroups] = useState([]);
    const history = useHistory();

    useEffect(() => {
        getSuperGroups().then(response => {
            setSuperGroups(response.data);
        });
    }, []);

    if (superGroups.length === 0) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    return (
        <DigitCRUD
            formInitialValues={initialValues()}
            formValidationSchema={validationSchema(text)}
            formComponentData={keysComponentData(text, superGroups)}
            keysOrder={keysOrder()}
            readOneKeysOrder={readOneKeysOrder()}
            readAllKeysOrder={readAllKeysOrder()}
            updateKeysOrder={updateKeysOrder()}
            keysText={keysText(text)}
            canDelete={() => admin}
            canUpdate={() => admin} //|| inGroup(user, group)}
            name={"groups"}
            path={"/groups"}
            readAllRequest={getGroupsMinified}
            readOneRequest={getGroup}
            deleteRequest={deleteGroup}
            updateRequest={(id, data) =>
                editGroup(id, {
                    name: data.name,
                    email: data.email,
                    superGroup: data.superGroup,
                    prettyName: data.prettyName,
                    version: data.version
                })
            }
            createRequest={
                admin
                    ? data =>
                          addGroup({
                              name: data[GROUP_NAME],
                              email: data[GROUP_EMAIL],
                              superGroup: data[GROUP_SUPER_GROUP],
                              prettyName: data[GROUP_PRETTY_NAME]
                          })
                    : null
            }
            tableProps={{
                orderBy: GROUP_NAME,
                startOrderBy: GROUP_NAME,
                titleText: text.Groups,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            idProp={GROUP_ID}
            detailsRenderStart={data => (
                <DigitLayout.Row
                    size={{ maxWidth: "500px", maxHeight: "300px" }}
                    padding={"8px"}
                >
                    <img
                        style={{
                            width: "20%",
                            height: "auto",
                            objectFit: "contain"
                        }}
                        src={"/api/internal/groups/avatar/" + data.id}
                    />
                    <img
                        style={{
                            marginLeft: "5%",
                            width: "75%",
                            height: "auto",
                            objectFit: "contain"
                        }}
                        src={"/api/internal/groups/banner/" + data.id}
                    />
                </DigitLayout.Row>
            )}
            detailsRenderCardEnd={data =>
                admin ? (
                    <DigitLayout.Row
                        flexWrap={"wrap"}
                        justifyContent={"center"}
                    >
                        <DigitButton
                            alignSelf={"center"}
                            size={{ width: "max-content" }}
                            margin={"8px"}
                            outlined
                            text={text.EditMembers}
                            onClick={() => history.push("/members/" + data.id)}
                        />
                        <DigitButton
                            alignSelf={"center"}
                            size={{ width: "max-content" }}
                            margin={"8px"}
                            outlined
                            text={text.EditBanner}
                            onClick={() =>
                                history.push("/groups/" + data.id + "/banner")
                            }
                        />
                        <DigitButton
                            alignSelf={"center"}
                            size={{ width: "auto" }}
                            margin={"8px"}
                            outlined
                            text={text.EditAvatar}
                            onClick={() =>
                                history.push("/groups/" + data.id + "/avatar")
                            }
                        />
                    </DigitLayout.Row>
                ) : null
            }
            detailsRenderEnd={data => (
                <DigitLayout.Row flexWrap={"wrap"} justifyContent={"center"}>
                    <DisplayMembersTable
                        margin={{
                            top: "16px"
                        }}
                        noUsersText={text.NoGroupMembers}
                        users={data[GROUP_MEMBERS].map(member => ({
                            ...member.user,
                            post: member.post,
                            unofficialPostName: member.unofficialPostName
                        }))}
                        group={data}
                    />
                </DigitLayout.Row>
            )}
            createButtonText={text.Create + " " + text.Group}
            updateTitle={group => text.Update + " " + group[GROUP_PRETTY_NAME]}
            createTitle={text.CreateGroup}
            detailsTitle={group => group[GROUP_PRETTY_NAME]}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            toastCreateSuccessful={() => text.GroupWasCreated}
            toastCreateFailed={() => text.GroupDeleteFailed}
            toastDeleteSuccessful={() => text.GroupWasDeleted}
            toastDeleteFailed={() => text.GroupDeleteFailed}
            toastUpdateSuccessful={one =>
                one[GROUP_PRETTY_NAME] + text.GroupWasUpdated
            }
            toastUpdateFailed={() => text.GroupUpdateFailed}
            backButtonText={text.Back}
            updateButtonText={() => text.Edit}
            deleteButtonText={one => text.Delete + " " + one[GROUP_PRETTY_NAME]}
            detailsButtonText={text.Details}
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={one =>
                text.AreYouSureYouWantToDelete +
                " " +
                one[GROUP_PRETTY_NAME] +
                "?"
            }
            useHistoryGoBackOnBack
        />
    );
};

export default GroupsCrud;
