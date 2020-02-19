import React from "react";
import {
    DigitTextField,
    useDigitTranslations,
    DigitEditDataCard,
    useDigitToast,
    DigitLayout
} from "@cthit/react-digit-components";
import * as yup from "yup";
import translations from "./ResetPasswordFinish.screen.translations";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import { useHistory } from "react-router";
import { resetPasswordFinalize } from "../../../../api/reset-password/put.reset-password";

const ResetPasswordFinish = () => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const history = useHistory();

    return (
        <DigitLayout.Center>
            <DigitEditDataCard
                validationSchema={yup.object().shape({
                    cid: yup.string().required(text.FieldRequired),
                    token: yup.string().required(text.FieldRequired),
                    password: yup
                        .string()
                        .min(8, text.MinimumLength)
                        .required(text.FieldRequired),
                    passwordConfirmation: yup
                        .string()
                        .oneOf([yup.ref("password")], text.PasswordDoNotMatch)
                        .required(text.FieldRequired)
                })}
                initialValues={{
                    cid: "",
                    code: "",
                    password: "",
                    passwordConfirmation: ""
                }}
                onSubmit={(values, actions) => {
                    resetPasswordFinalize(values)
                        .then(response => {
                            actions.resetForm();
                            actions.setSubmitting(false);
                            history.push("/login");
                        })
                        .catch(error => {
                            const code = statusCode(error);
                            const message = statusMessage(error);
                            let errorMessage = text.SomethingWentWrong;
                            if (code === 422) {
                                switch (message) {
                                    case "CODE_OR_CID_IS_WRONG":
                                        errorMessage = text.CodeOrCidIsWrong;
                                        break;
                                    case "TOO_SHORT_PASSWORD":
                                        errorMessage = text.PasswordTooShort;
                                        break;
                                    default:
                                        errorMessage = text.SomethingWentWrong;
                                }
                            }
                            queueToast({
                                text: errorMessage,
                                duration: 5000
                            });
                        });
                }}
                keysOrder={["cid", "token", "password", "passwordConfirmation"]}
                keysComponentData={{
                    cid: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Cid,
                            filled: false,
                            outlined: true
                        }
                    },
                    token: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Code,
                            filled: false,
                            outlined: true
                        }
                    },
                    password: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Password,
                            password: true,
                            outlined: true
                        }
                    },
                    passwordConfirmation: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.PasswordConfirmation,
                            password: true,
                            outlined: true
                        }
                    }
                }}
                submitText={text.ResetPassword}
                subtitleText={text.ResetDescription}
                titleText={text.PasswordResetTitle}
            />
        </DigitLayout.Center>
    );
};

export default ResetPasswordFinish;
