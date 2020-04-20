import React from "react";
import {
    DigitTextField,
    DigitEditDataCard,
    useDigitTranslations,
    useDigitToast,
    DigitDesign,
    DigitText,
    DigitLayout
} from "@cthit/react-digit-components";
import translations from "./ResetPasswordInitialize.screen.translations";
import * as yup from "yup";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import { resetPasswordInitialize } from "../../../../api/reset-password/post.reset-password";
import { useHistory, useLocation } from "react-router-dom";

const ResetPasswordInitialize = () => {
    const [text] = useDigitTranslations(translations);
    const history = useHistory();
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
                centerFields
                validationSchema={yup.object().shape({
                    cid: yup.string().required(text.FieldRequired)
                })}
                initialValues={{ cid: "" }}
                onSubmit={(values, actions) => {
                    resetPasswordInitialize(values)
                        .then(() => {
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
                size={{ width: "300px", height: "300px" }}
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
