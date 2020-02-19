import React from "react";
import {
    DigitEditData,
    useDigitTranslations,
    DigitTextField,
    DigitLayout
} from "@cthit/react-digit-components";
import * as yup from "yup";
import translations from "./MeChangePassword.screen.translations";
const MeChangePassword = () => {
    const [text] = useDigitTranslations(translations);

    return null;

    return (
        <DigitLayout.Center>
            <DigitEditData
                onSubmit={v => {
                    console.log(v);
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
                titleText={text.ChangePasswordOn + " " /*me.nick*/}
            />
        </DigitLayout.Center>
    );
};

export default MeChangePassword;
