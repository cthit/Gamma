import React from "react";
import { useHistory } from "react-router-dom";
import * as yup from "yup";

import {
    DigitEditDataCard,
    useDigitTranslations,
    DigitTextField,
    DigitLayout,
    useDigitToast
} from "@cthit/react-digit-components";

import { editPassword } from "api/me/put.me.api";

import useGammaUser from "common/hooks/use-gamma-user/useGammaUser";

import translations from "./MeChangePassword.screen.translations";

const MeChangePassword = () => {
    const me = useGammaUser();
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const history = useHistory();

    return (
        <DigitLayout.Center>
            <DigitEditDataCard
                centerFields
                onSubmit={v => {
                    const { oldPassword, password } = v;
                    editPassword({
                        oldPassword,
                        password
                    })
                        .then(() => {
                            queueToast({ text: text.PasswordWasChanged });
                            history.push("/me");
                        })
                        .catch(() =>
                            queueToast({
                                text: text.SomethingWentWrongWhenChangingPassword
                            })
                        );
                }}
                validationSchema={yup.object().shape({
                    oldPassword: yup.string(),
                    password: yup
                        .string()
                        .min(8, text.MinimumLength)
                        .required(text.FieldRequired),
                    confirmNewPassword: yup
                        .string()
                        .oneOf([yup.ref("password")], text.PasswordDoNotMatch)
                        .required(text.FieldRequired)
                })}
                keysComponentData={{
                    oldPassword: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.OldPassword,
                            password: true,
                            outlined: true
                        }
                    },
                    password: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.NewPassword,
                            password: true,
                            outlined: true
                        }
                    },
                    confirmNewPassword: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.ConfirmNewPassword,
                            password: true,
                            outlined: true
                        }
                    }
                }}
                initialValues={{
                    oldPassword: "",
                    password: "",
                    confirmNewPassword: ""
                }}
                keysOrder={["oldPassword", "password", "confirmNewPassword"]}
                submitText={text.ChangePassword}
                titleText={text.ChangePasswordOn + " " + me.nick}
                extraButton={{
                    onClick: () => history.goBack(),
                    text: text.Back,
                    outlined: true
                }}
            />
        </DigitLayout.Center>
    );
};

export default MeChangePassword;
