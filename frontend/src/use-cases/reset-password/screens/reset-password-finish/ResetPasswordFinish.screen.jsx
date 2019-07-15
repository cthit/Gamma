import React from "react";
import {
    DigitTranslations,
    DigitLayout,
    DigitForm,
    DigitDesign,
    DigitTextField,
    DigitFormField,
    DigitButton
} from "@cthit/react-digit-components";
import * as yup from "yup";
import translations from "./ResetPasswordFinish.screen.translations";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";

class ResetPasswordFinish extends React.Component {
    componentDidMount() {
        const { gammaLoadingFinished } = this.props;
        gammaLoadingFinished();
    }

    render() {
        const {
            sendPasswordResetFinish,
            redirectTo,
            toastOpen,
            gammaLoadingStart,
            gammaLoadingFinished
        } = this.props;
        return (
            <div>
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <DigitLayout.MarginTop>
                            <DigitLayout.Center>
                                <DigitForm
                                    validationSchema={yup.object().shape({
                                        cid: yup
                                            .string()
                                            .required(text.FieldRequired),
                                        token: yup
                                            .string()
                                            .required(text.FieldRequired),
                                        password: yup
                                            .string()
                                            .min(8, text.MinimumLength)
                                            .required(text.FieldRequired),
                                        passwordConfirmation: yup
                                            .string()
                                            .oneOf(
                                                [yup.ref("password")],
                                                text.PasswordDoNotMatch
                                            )
                                            .required(text.FieldRequired)
                                    })}
                                    initialValues={{
                                        cid: "",
                                        code: "",
                                        password: "",
                                        passwordConfirmation: ""
                                    }}
                                    onSubmit={(values, actions) => {
                                        gammaLoadingStart();
                                        sendPasswordResetFinish(values)
                                            .then(response => {
                                                actions.resetForm();
                                                actions.setSubmitting(false);
                                                redirectTo("/login");
                                            })
                                            .catch(error => {
                                                const code = statusCode(error);
                                                const message = statusMessage(
                                                    error
                                                );
                                                let errorMessage =
                                                    text.SomethingWentWrong;
                                                if (code === 422) {
                                                    switch (message) {
                                                        case "CODE_OR_CID_IS_WRONG":
                                                            errorMessage =
                                                                text.CodeOrCidIsWrong;
                                                            break;
                                                        case "TOO_SHORT_PASSWORD":
                                                            errorMessage =
                                                                text.PasswordTooShort;
                                                            break;
                                                        default:
                                                            errorMessage =
                                                                text.SomethingWentWrong;
                                                    }
                                                }
                                                toastOpen({
                                                    text: errorMessage,
                                                    duration: 5000
                                                });
                                            });
                                        gammaLoadingFinished();
                                    }}
                                    render={({ errors, touched }) => (
                                        <DigitDesign.Card
                                            absWidth="400px"
                                            absHeight="500px"
                                            hasSubTitle
                                        >
                                            <DigitDesign.CardTitle
                                                text={text.PasswordResetTitle}
                                            />
                                            <DigitDesign.CardSubTitle
                                                text={text.ResetDescription}
                                            />
                                            <DigitDesign.CardBody>
                                                <DigitLayout.Center>
                                                    <DigitFormField
                                                        name="cid"
                                                        component={
                                                            DigitTextField
                                                        }
                                                        componentProps={{
                                                            upperLabel:
                                                                text.Cid,
                                                            filled: false,
                                                            outlined: true
                                                        }}
                                                    />
                                                    <DigitFormField
                                                        name="token"
                                                        component={
                                                            DigitTextField
                                                        }
                                                        componentProps={{
                                                            upperLabel:
                                                                text.Code,
                                                            filled: false,
                                                            outlined: true
                                                        }}
                                                    />
                                                    <DigitFormField
                                                        name="password"
                                                        component={
                                                            DigitTextField
                                                        }
                                                        componentProps={{
                                                            upperLabel:
                                                                text.Password,
                                                            password: true,
                                                            outlined: true
                                                        }}
                                                    />
                                                    <DigitFormField
                                                        name="passwordConfirmation"
                                                        component={
                                                            DigitTextField
                                                        }
                                                        componentProps={{
                                                            upperLabel:
                                                                text.PasswordConfirmation,
                                                            password: true,
                                                            outlined: true
                                                        }}
                                                    />
                                                </DigitLayout.Center>
                                            </DigitDesign.CardBody>
                                            <DigitDesign.CardButtons
                                                reverseDirection
                                            >
                                                <DigitButton
                                                    text={text.ResetPassword}
                                                    primary
                                                    raised
                                                    submit
                                                />
                                            </DigitDesign.CardButtons>
                                        </DigitDesign.Card>
                                    )}
                                />
                            </DigitLayout.Center>
                        </DigitLayout.MarginTop>
                    )}
                />
            </div>
        );
    }
}

export default ResetPasswordFinish;
