import React, { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import * as yup from "yup";

import {
    DigitLayout,
    DigitEditDataCard,
    DigitTextField,
    useDigitTranslations,
    useDigitToast
} from "@cthit/react-digit-components";

import { getUser } from "api/users/get.users.api";
import { editPasswordAdmin } from "api/users/put.users.api";

import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import translations from "./ResetPasswordAdmin.screen.translations";

const ResetPasswordAdmin = () => {
    const [text] = useDigitTranslations(translations);
    const admin = useGammaIsAdmin();
    const history = useHistory();
    const [queueToast] = useDigitToast();
    const [user, setUser] = useState(null);
    const { id } = useParams();

    useEffect(() => {
        getUser(id).then(response => {
            setUser(response.data);
        });
    }, [id]);

    if (!admin) {
        return <InsufficientAccess />;
    }

    if (user == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    return (
        <DigitLayout.Center>
            <DigitEditDataCard
                centerFields
                titleText={text.ChangePasswordFor + " " + user.nick}
                submitText={text.ChangePassword}
                keysOrder={["password"]}
                onSubmit={({ password }, actions) => {
                    editPasswordAdmin(user.id, { password })
                        .then(() => {
                            queueToast({
                                text: text.PasswordChanged
                            });
                            actions.resetForm();
                            history.goBack();
                        })
                        .catch(() => {
                            queueToast({
                                text: text.SomethingWentWrong
                            });
                        });
                }}
                keysComponentData={{
                    password: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.NewPassword,
                            outlined: true
                        }
                    }
                }}
                extraButton={{
                    text: text.Back,
                    outlined: true,
                    onClick: () => history.goBack()
                }}
                validationSchema={yup.object().shape({
                    password: yup
                        .string()
                        .min(8, text.MinimumLength)
                        .required(text.FieldRequired)
                })}
                initialValues={{
                    password: ""
                }}
            />
        </DigitLayout.Center>
    );
};

export default ResetPasswordAdmin;
