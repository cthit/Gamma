import {
    DigitDesign,
    DigitForm,
    DigitFormField,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";
import PropTypes from "prop-types";
import React from "react";
import * as yup from "yup";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import {
    DigitButton,
    DigitSwitch,
    DigitTextField,
    DigitSelect
} from "@cthit/react-digit-components";
import translations from "./InputDataAndCode.view.translations.json";

class InputDataAndCode extends React.Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        const {
            sendDataAndCode,
            toastOpen,
            redirectTo,
            gammaLoadingStart
        } = this.props;

        return (
            <DigitTranslations
                translations={translations}
                uniquePath="CreateAccount"
                render={text => (
                    <DigitLayout.Center>
                        <DigitForm
                            onSubmit={(values, actions) => {
                                gammaLoadingStart();

                                const cid = values.cid;
                                const user = {
                                    whitelist: {
                                        cid: cid
                                    },
                                    ...values
                                };
                                sendDataAndCode(user)
                                    .then(response => {
                                        actions.resetForm();
                                        redirectTo("/create-account/finished");
                                    })
                                    .catch(error => {
                                        const code = statusCode(error);
                                        const message = statusMessage(error);
                                        var errorMessage =
                                            text.SomethingWentWrong;
                                        switch (code) {
                                            case 422:
                                                switch (message) {
                                                    case "CODE_OR_CID_IS_WRONG":
                                                        errorMessage =
                                                            text.CODE_OR_CID_IS_WRONG;
                                                        break;
                                                    case "TOO_SHORT_PASSWORD":
                                                        errorMessage =
                                                            text.TOO_SHORT_PASSWORD;
                                                        break;
                                                    default:
                                                        errorMessage =
                                                            text.SomethingWentWrong;
                                                }
                                                break;
                                            default:
                                                errorMessage =
                                                    text.SomethingWentWrong;
                                        }
                                        toastOpen({
                                            text: errorMessage,
                                            duration: 5000
                                        });
                                        this.props.gammaLoadingFinished();
                                    });
                            }}
                            initialValues={{
                                cid: "",
                                code: "",
                                nick: "",
                                firstName: "",
                                lastName: "",
                                acceptanceYear: "",
                                password: "",
                                passwordConfirmation: "",
                                userAgreement: false
                            }}
                            validationSchema={yup.object().shape({
                                cid: yup.string().required(text.FieldRequired),
                                code: yup.string().required(text.FieldRequired),
                                nick: yup.string().required(text.FieldRequired),
                                firstName: yup
                                    .string()
                                    .required(text.FieldRequired),
                                lastName: yup
                                    .string()
                                    .required(text.FieldRequired),
                                acceptanceYear: yup
                                    .number()
                                    .min(2001)
                                    .max(_getCurrentYear())
                                    .required(text.FieldRequired),
                                password: yup
                                    .string()
                                    .min(8, text.MinimumLength)
                                    .required(text.FieldRequired),
                                passwordConfirmation: yup
                                    .string()
                                    .oneOf(
                                        [yup.ref("password")],
                                        text.PasswordsDoNotMatch
                                    )
                                    .required(text.FieldRequired),
                                userAgreement: yup
                                    .boolean()
                                    .oneOf([true])
                                    .required(text.FieldRequired)
                            })}
                            render={props => (
                                <DigitDesign.Card
                                    minWidth="320px"
                                    maxWidth="600px"
                                    hasSubTitle
                                >
                                    <DigitDesign.CardTitle
                                        text={text.CompleteCreation}
                                    />
                                    <DigitDesign.CardSubTitle
                                        text={text.CompleteCreationDescription}
                                    />
                                    <DigitDesign.CardBody>
                                        <DigitLayout.Center>
                                            <DigitFormField
                                                name="cid"
                                                component={DigitTextField}
                                                componentProps={{
                                                    upperLabel: text.YourCid,
                                                    outlined: true
                                                }}
                                            />
                                            <DigitLayout.Spacing />
                                            <DigitFormField
                                                name="code"
                                                component={DigitTextField}
                                                componentProps={{
                                                    upperLabel:
                                                        text.CodeFromYourStudentEmail,
                                                    outlined: true
                                                }}
                                            />
                                            <DigitLayout.Spacing />
                                            <DigitFormField
                                                name="nick"
                                                component={DigitTextField}
                                                componentProps={{
                                                    upperLabel: text.Nick,
                                                    outlined: true
                                                }}
                                            />
                                            <DigitLayout.Spacing />
                                            <DigitFormField
                                                name="password"
                                                component={DigitTextField}
                                                componentProps={{
                                                    upperLabel: text.Password,
                                                    password: true,
                                                    outlined: true
                                                }}
                                            />
                                            <DigitLayout.Spacing />
                                            <DigitFormField
                                                name="passwordConfirmation"
                                                component={DigitTextField}
                                                componentProps={{
                                                    upperLabel:
                                                        text.ConfirmPassword,
                                                    password: true,
                                                    outlined: true
                                                }}
                                            />
                                            <DigitLayout.Spacing />
                                            <DigitFormField
                                                name="firstName"
                                                component={DigitTextField}
                                                componentProps={{
                                                    upperLabel: text.FirstName,
                                                    outlined: true
                                                }}
                                            />
                                            <DigitLayout.Spacing />
                                            <DigitFormField
                                                name="lastName"
                                                component={DigitTextField}
                                                componentProps={{
                                                    upperLabel: text.LastName,
                                                    outlined: true
                                                }}
                                            />
                                            <DigitLayout.Spacing />
                                            <DigitLayout.Size width="300px">
                                                <DigitFormField
                                                    name="acceptanceYear"
                                                    component={DigitSelect}
                                                    componentProps={{
                                                        valueToTextMap: _generateAcceptanceYears(),
                                                        upperLabel:
                                                            text.WhichYearDidYouStart,
                                                        reverse: true,
                                                        outlined: true
                                                    }}
                                                />
                                            </DigitLayout.Size>
                                            <DigitLayout.Spacing />
                                            <DigitFormField
                                                name="userAgreement"
                                                component={DigitSwitch}
                                                componentProps={{
                                                    label:
                                                        text.AcceptUserAgreement,
                                                    primary: true
                                                }}
                                            />
                                        </DigitLayout.Center>
                                    </DigitDesign.CardBody>
                                    <DigitDesign.CardButtons
                                        leftRight
                                        reverseDirection
                                    >
                                        <DigitButton
                                            submit
                                            text={text.CreateAccount}
                                            primary
                                            raised
                                        />
                                        <DigitLayout.Spacing />
                                    </DigitDesign.CardButtons>
                                </DigitDesign.Card>
                            )}
                        />
                    </DigitLayout.Center>
                )}
            />
        );
    }
}
function _getCurrentYear() {
    return new Date().getFullYear() + "";
}

function _generateAcceptanceYears() {
    const output = {};
    const startYear = 2001;
    const currentYear = _getCurrentYear();
    for (var i = currentYear; i >= startYear; i--) {
        output[i] = i + "";
    }
    return output;
}

InputDataAndCode.propTypes = {
    sendDataAndCode: PropTypes.func.isRequired
};

export default InputDataAndCode;
