import React from "react";
import { useHistory } from "react-router-dom";
import * as yup from "yup";

import {
    DigitTextField,
    DigitEditDataCard,
    useDigitTranslations,
    useDigitToast,
    DigitLayout
} from "@cthit/react-digit-components";

import { resetPasswordInitialize } from "api/reset-password/post.reset-password";

import statusCode from "common/utils/formatters/statusCode.formatter";
import statusMessage from "common/utils/formatters/statusMessage.formatter";
import ChangeLanguageLocally from "common/views/change-language-locally";

import translations from "./ResetPasswordInitialize.screen.translations";

const ResetPasswordInitialize = () => {
    const [text] = useDigitTranslations(translations);
    const history = useHistory();
    const [queueToast] = useDigitToast();

    return (
        <DigitLayout.Center>
            <ChangeLanguageLocally />
            <DigitEditDataCard
                centerFields
                validationSchema={yup.object().shape({
                    email: yup.string().required(text.FieldRequired)
                })}
                initialValues={{ cidOrEmail: "" }}
                onSubmit={(values, actions) => {
                    resetPasswordInitialize(values)
                        .then(() => {
                            actions.resetForm();
                            actions.setSubmitting(false);
                            history.push("/reset-password/finish");
                        })
                        .catch(() => {
                            let errorMessage = text.SomethingWentWrong;
                            queueToast({
                                text: errorMessage,
                                duration: 5000
                            });
                        });
                }}
                titleText={text.PasswordResetTitle}
                subtitleText={text.ResetDescription}
                submitText={text.ResetPassword}
                size={{ width: "300px", height: "300px" }}
                keysComponentData={{
                    email: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Email,
                            outlined: true
                        }
                    }
                }}
                keysOrder={["email"]}
            />
        </DigitLayout.Center>
    );
};

export default ResetPasswordInitialize;
