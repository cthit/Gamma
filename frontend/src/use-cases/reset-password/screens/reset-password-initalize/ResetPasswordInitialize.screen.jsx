import React from "react";
import {
    DigitTextField,
    DigitEditDataCard,
    useDigitTranslations,
    useDigitToast,
    DigitLayout
} from "@cthit/react-digit-components";
import translations from "./ResetPasswordInitialize.screen.translations";
import * as yup from "yup";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import { resetPasswordInitialize } from "../../../../api/reset-password/post.reset-password";
import { useHistory } from "react-router";

const ResetPasswordInitialize = () => {
    const [text] = useDigitTranslations(translations);
    const history = useHistory();
    const [queueToast] = useDigitToast();

    return (
        <DigitLayout.Center>
            <DigitEditDataCard
                validationSchema={yup.object().shape({
                    cid: yup.string().required(text.FieldRequired)
                })}
                initialValues={{ cid: "" }}
                onSubmit={(values, actions) => {
                    resetPasswordInitialize(values)
                        .then(response => {
                            actions.resetForm();
                            actions.setSubmitting(false);
                            history.push("/reset-password/finish");
                        })
                        .catch(error => {
                            const code = statusCode(error);
                            const message = statusMessage(error);
                            let errorMessage = text.SomethingWentWrong;
                            if (code === 422) {
                                if (message === "NO_USER_FOUND") {
                                    errorMessage = text.CredentialsDoNotMatch;
                                }
                            }
                            queueToast({
                                text: errorMessage,
                                duration: 5000
                            });
                        });
                }}
                titleText={text.PasswordResetTitle}
                subtitleText={text.ResetDescription}
                submitText={text.ResetPassword}
                absWidth={"300px"}
                absHeight={"250px"}
                keysComponentData={{
                    cid: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Cid,
                            outlined: true
                        }
                    }
                }}
                keysOrder={["cid"]}
            />
        </DigitLayout.Center>
    );
};

export default ResetPasswordInitialize;
