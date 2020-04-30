import React from "react";
import {
    DigitTextField,
    useDigitTranslations,
    DigitEditDataCard,
    useDigitToast,
    DigitDesign,
    DigitText,
    DigitLayout
} from "@cthit/react-digit-components";
import * as yup from "yup";
import translations from "./ResetPasswordFinish.screen.translations";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import { resetPasswordFinalize } from "../../../../api/reset-password/put.reset-password";
import { getBackendUrl } from "../../../../common/utils/configs/envVariablesLoader";
import { useLocation } from "react-router-dom";

const ResetPasswordFinish = () => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const { search } = useLocation();
    const accountLocked = search.includes("accountLocked=true");

    return (
        <DigitLayout.Center>
            {accountLocked && (
                <DigitDesign.Card
                    size={{ width: "300px" }}
                    margin={{ bottom: "16px" }}
                >
                    <DigitDesign.CardHeader>
                        <DigitDesign.CardTitle
                            text={"Hey! You're finally awake!"}
                        />
                    </DigitDesign.CardHeader>
                    <DigitDesign.CardHeaderImage src="/awake.gif" />
                    <DigitDesign.CardBody>
                        <DigitText.Text text={text.AccountLocked} />
                    </DigitDesign.CardBody>
                </DigitDesign.Card>
            )}
            <DigitEditDataCard
                size={{ width: "300px" }}
                centerFields
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
                        .then(() => {
                            actions.resetForm();
                            actions.setSubmitting(false);
                            queueToast({
                                text: text.Success,
                                duration: 5000
                            });
                            setTimeout(() => {
                                window.location.href =
                                    getBackendUrl() + "/login";
                            }, 5000);
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
