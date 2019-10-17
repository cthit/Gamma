import React, { useEffect, useState } from "react";
import { Route, Switch } from "react-router-dom";
import ChangeUserPassword from "./screens/change-user-password";
import { DigitCRUD, useDigitTranslations } from "@cthit/react-digit-components";
import { getUser, getUsersMinified } from "../../api/users/get.users.api";
import translations from "./Users.translations";
import {
    ACCEPTANCE_YEAR,
    CID,
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../api/users/props.users.api";
import { useDispatch, useSelector } from "react-redux";
import { deltaLoadingFinished } from "../../app/views/delta-loading/DeltaLoading.view.action-creator";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import { editUser } from "../../api/users/put.users.api";
import { deleteUser } from "../../api/users/delete.users.api";
import {
    generateUserCustomDetailsRenders,
    generateUserEditComponentData,
    generateUserKeysTexts,
    generateUserValidationSchema
} from "../../common/utils/generators/user-form.generator";
import { getWebsites } from "../../api/websites/get.websites.api";

const Users = () => {
    const admin = useIsAdmin();
    const [text] = useDigitTranslations(translations);
    const [websites, setWebsites] = useState(null);
    const dispatch = useDispatch();
    useEffect(() => {
        getWebsites().then(response => {
            setWebsites(response.data);
            dispatch(deltaLoadingFinished());
        });
    }, []);

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
                            idProp={ID}
                            keysOrder={[
                                ID,
                                FIRST_NAME,
                                LAST_NAME,
                                CID,
                                NICK,
                                ACCEPTANCE_YEAR
                            ]}
                            tableProps={{
                                titleText: text.Groups,
                                startOrderBy: NICK,
                                search: true
                            }}
                            customDetailsRenders={generateUserCustomDetailsRenders()}
                            keysText={generateUserKeysTexts(text)}
                            formValidationSchema={generateUserValidationSchema(
                                text
                            )}
                            formComponentData={generateUserEditComponentData(
                                text,
                                websites
                            )}
                        />
                    )}
                />
            </Switch>
        </>
    );
};

export default Users;
