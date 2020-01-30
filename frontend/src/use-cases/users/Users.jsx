import React, { useEffect, useState } from "react";
import { Route, Switch } from "react-router-dom";
import ChangeUserPassword from "./screens/change-user-password";
import { DigitCRUD, useDigitTranslations } from "@cthit/react-digit-components";
import { getUser, getUsersMinified } from "../../api/users/get.users.api";
import translations from "./Users.translations";
import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    GROUPS,
    ID,
    LANGUAGE,
    LAST_NAME,
    NICK,
    PASSWORD,
    USER_AGREEMENT,
    WEBSITES
} from "../../api/users/props.users.api";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import useIsAdmin from "../../common/hooks/use-is/use-is-admin";
import { editUser } from "../../api/users/put.users.api";
import { deleteUser } from "../../api/users/delete.users.api";
import {
    generateUserCustomDetailsRenders,
    generateUserEditComponentData,
    generateUserInitialValues,
    generateUserKeysTexts,
    generateUserValidationSchema
} from "../../common/utils/generators/user-form.generator";
import { getWebsites } from "../../api/websites/get.websites.api";
import { addUser } from "../../api/users/post.users.api";

const Users = () => {
    const admin = useIsAdmin();
    const [text] = useDigitTranslations(translations);
    const [websites, setWebsites] = useState(null);
    const dispatch = useDispatch();
    useEffect(() => {
        getWebsites().then(response => {
            setWebsites(response.data);
            dispatch(gammaLoadingFinished());
        });
    }, [dispatch]);

    const fullName = data =>
        data[FIRST_NAME] + " '" + data[NICK] + "' " + data[LAST_NAME];

    if (websites == null) {
        return null;
    }

    return (
        <>
            <Switch>
                <Route
                    path="/users/:id/change_password"
                    exact
                    component={ChangeUserPassword}
                />
                <Route
                    path={"/users"}
                    render={() => (
                        <DigitCRUD
                            name={"users"}
                            path={"/users"}
                            readAllRequest={getUsersMinified}
                            readOneRequest={admin ? getUser : null}
                            updateRequest={admin ? editUser : null}
                            deleteRequest={admin ? deleteUser : null}
                            createRequest={admin ? addUser : null}
                            idProp={ID}
                            keysOrder={[
                                CID,
                                PASSWORD,
                                FIRST_NAME,
                                LAST_NAME,
                                NICK,
                                EMAIL,
                                ACCEPTANCE_YEAR,
                                LANGUAGE,
                                WEBSITES,
                                USER_AGREEMENT,
                                GROUPS
                            ]}
                            tableProps={{
                                titleText: text.Users,
                                startOrderBy: NICK,
                                search: true,
                                columnsOrder: [
                                    CID,
                                    FIRST_NAME,
                                    NICK,
                                    LAST_NAME,
                                    ACCEPTANCE_YEAR
                                ]
                            }}
                            customDetailsRenders={generateUserCustomDetailsRenders(
                                text
                            )}
                            keysText={generateUserKeysTexts(text)}
                            formValidationSchema={generateUserValidationSchema(
                                text,
                                true,
                                true,
                                true
                            )}
                            updateFormValidationSchema={() =>
                                generateUserValidationSchema(
                                    text,
                                    true,
                                    true,
                                    false
                                )
                            }
                            formComponentData={generateUserEditComponentData(
                                text,
                                websites
                            )}
                            backButtonText={text.Back}
                            detailsButtonText={text.Details}
                            createButtonText={text.Create}
                            updateButtonText={data =>
                                text.Update + " " + data[NICK]
                            }
                            deleteButtonText={data =>
                                text.Delete + " " + data[NICK]
                            }
                            dialogDeleteTitle={() => text.AreYouSure}
                            dialogDeleteDescription={data =>
                                text.AreYouSureYouWantToDelete +
                                " " +
                                fullName(data) +
                                "?"
                            }
                            detailsTitle={data => fullName(data)}
                            dialogDeleteConfirm={() => text.Delete}
                            dialogDeleteCancel={() => text.Cancel}
                            createTitle={text.CreateUser}
                            updateTitle={data =>
                                text.Update + " " + fullName(data)
                            }
                            toastUpdateSuccessful={data =>
                                fullName(data) +
                                " " +
                                text.WasUpdatedSuccessfully
                            }
                            toastUpdateFailed={data =>
                                text.UserUpdateFailed1 +
                                " " +
                                fullName(data) +
                                " " +
                                text.UserUpdateFailed2
                            }
                            toastDeleteSuccessful={data =>
                                fullName(data) +
                                " " +
                                text.WasDeletedSuccessfully
                            }
                            toastDeleteFailed={data =>
                                text.UserDeletionFailed1 +
                                " " +
                                fullName(data) +
                                " " +
                                text.UserDeletionFailed2
                            }
                            toastCreateSuccessful={data =>
                                fullName(data) +
                                " " +
                                text.WasCreatedSuccessfully
                            }
                            toastCreateFailed={() => text.FailedCreatingUser}
                            formInitialValues={generateUserInitialValues()}
                        />
                    )}
                />
            </Switch>
        </>
    );
};

export default Users;
