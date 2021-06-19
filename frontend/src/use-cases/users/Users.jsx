import React from "react";
import { useHistory } from "react-router-dom";
import styled from "styled-components";

import {
    DigitButton,
    DigitCRUD,
    DigitLayout,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { GROUP_PRETTY_NAME } from "api/groups/props.groups.api";
import { deleteUser } from "api/users/delete.users.api";
import {
    getUser,
    getUserAdmin,
    getUsersMinified
} from "api/users/get.users.api";
import { addUser } from "api/users/post.users.api";
import {
    USER_FIRST_NAME,
    USER_ID,
    USER_LAST_NAME,
    USER_NICK,
    USER_GROUPS
} from "api/users/props.users.api";
import { editUser } from "api/users/put.users.api";

import DisplayGroupsTable from "common/elements/display-groups-table/DisplayGroupsTable.element";
import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import {
    generateUserCustomDetailsRenders,
    generateUserEditComponentData
} from "common/utils/generators/user-form.generator";
import InsufficientAccess from "common/views/insufficient-access";

import FiveZeroZero from "../../app/elements/five-zero-zero";
import FourOFour from "../four-o-four";
import {
    createKeysOrder,
    createValidationSchema,
    initialValues,
    keysOrder,
    keysText,
    readAllKeysOrder,
    readOneKeysOrder,
    updateKeysOrder,
    updateValidationSchema
} from "./Users.options";
import translations from "./Users.translations";
import UserGroupsTable from "./usergroupstable";

const UserImage = styled.img`
    width: 250px;
    max-height: 500px;
    margin: auto;
`;

const Users = () => {
    const admin = useGammaIsAdmin();
    const [text] = useDigitTranslations(translations);
    const history = useHistory();

    const fullName = data =>
        data[USER_FIRST_NAME] +
        " '" +
        data[USER_NICK] +
        "' " +
        data[USER_LAST_NAME];

    return (
        <DigitCRUD
            keysOrder={keysOrder()}
            readOneKeysOrder={readOneKeysOrder()}
            readAllKeysOrder={readAllKeysOrder()}
            updateKeysOrder={updateKeysOrder()}
            createKeysOrder={createKeysOrder()}
            keysText={keysText(text)}
            createFormValidationSchema={createValidationSchema(text)}
            updateFormValidationSchema={() => updateValidationSchema(text)}
            formComponentData={generateUserEditComponentData(text)}
            name={"users"}
            path={"/users"}
            readAllRequest={getUsersMinified}
            readOneRequest={admin ? getUserAdmin : getUser}
            updateRequest={admin ? editUser : null}
            deleteRequest={admin ? deleteUser : null}
            createRequest={admin ? addUser : null}
            idProp={USER_ID}
            tableProps={{
                titleText: text.Users,
                startOrderBy: USER_NICK,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            customDetailsRenders={generateUserCustomDetailsRenders(text)}
            backButtonText={text.Back}
            detailsButtonText={text.Details}
            createButtonText={text.Create}
            updateButtonText={data => text.Update + " " + data[USER_NICK]}
            deleteButtonText={data => text.Delete + " " + data[USER_NICK]}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.AreYouSureYouWantToDelete + " " + fullName(data) + "?"
            }
            detailsTitle={data => fullName(data)}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteCancel={() => text.Cancel}
            createTitle={text.CreateUser}
            updateTitle={data => text.Update + " " + fullName(data)}
            toastUpdateSuccessful={data =>
                fullName(data) + " " + text.WasUpdatedSuccessfully
            }
            toastUpdateFailed={data =>
                text.UserUpdateFailed1 +
                " " +
                fullName(data) +
                " " +
                text.UserUpdateFailed2
            }
            toastDeleteSuccessful={data =>
                fullName(data) + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.UserDeletionFailed1 +
                " " +
                fullName(data) +
                " " +
                text.UserDeletionFailed2
            }
            toastCreateSuccessful={data =>
                fullName(data) + " " + text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingUser}
            formInitialValues={initialValues()}
            detailsRenderCardStart={data => (
                <UserImage
                    src={"/api/internal/users/avatar/" + data.id}
                    alt={"Profile picture"}
                />
            )}
            detailsRenderEnd={data => (
                <>
                    <UserGroupsTable
                        margin={{ top: "16px" }}
                        groups={data[USER_GROUPS]}
                        title={data[USER_NICK] + ":s " + text.Groups}
                        columnsOrder={[GROUP_PRETTY_NAME]}
                    />
                </>
            )}
            detailsRenderCardEnd={data =>
                admin ? (
                    <>
                        <div style={{ marginTop: "8px" }} />
                        <DigitLayout.Center>
                            <DigitButton
                                outlined
                                text={text.EditPassword}
                                onClick={() =>
                                    history.push(
                                        "/reset-password/admin/" + data[USER_ID]
                                    )
                                }
                            />
                        </DigitLayout.Center>
                    </>
                ) : null
            }
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            useHistoryGoBackOnBack
        />
    );
};

export default Users;
