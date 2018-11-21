import React from "react";
import PropTypes from "prop-types";
import * as yup from "yup";

import {
    AcceptUserAgreementInput,
    AcceptanceYearInput,
    ConfirmCidInput,
    ConfirmationCodeInput,
    CreateAccountButton,
    FirstnameInput,
    LastnameInput,
    NickInput,
    PasswordConfirmationInput,
    PasswordInput
} from "./InputDataAndCode.view.styles";

import { Center, Spacing } from "../../../../common-ui/layout";

import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";

import translations from "./InputDataAndCode.view.translations.json";
import {
    DigitTranslations,
    DigitForm,
    DigitFormField,
    DigitDesign
} from "@cthit/react-digit-components";

const InputDataAndCode = ({ sendDataAndCode, toastOpen, redirectTo }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <Center>
                <DigitForm
                    onSubmit={(values, actions) => {
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
                                var errorMessage = text.SomethingWentWrong;
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
                                        }
                                        break;
                                }
                                toastOpen({
                                    text: errorMessage,
                                    duration: 5000
                                });
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
                        firstName: yup.string().required(text.FieldRequired),
                        lastName: yup.string().required(text.FieldRequired),
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
                            minWidth="300px"
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
                                <Center>
                                    <DigitFormField
                                        name="cid"
                                        component={ConfirmCidInput}
                                        componentProps={{
                                            upperLabel: text.YourCid
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="code"
                                        component={ConfirmationCodeInput}
                                        componentProps={{
                                            upperLabel:
                                                text.CodeFromYourStudentEmail
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="nick"
                                        component={NickInput}
                                        componentProps={{
                                            upperLabel: text.Nick
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="password"
                                        component={PasswordInput}
                                        componentProps={{
                                            upperLabel: text.Password,
                                            password: true
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="passwordConfirmation"
                                        component={PasswordConfirmationInput}
                                        componentProps={{
                                            upperLabel: text.ConfirmPassword,
                                            password: true
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="firstName"
                                        component={FirstnameInput}
                                        componentProps={{
                                            upperLabel: text.FirstName
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="lastName"
                                        component={LastnameInput}
                                        componentProps={{
                                            upperLabel: text.LastName
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="acceptanceYear"
                                        component={AcceptanceYearInput}
                                        componentProps={{
                                            valueToTextMap: _generateAcceptanceYears(),
                                            upperLabel:
                                                text.WhichYearDidYouStart,
                                            reverse: true
                                        }}
                                    />
                                    <Spacing />
                                    <DigitFormField
                                        name="userAgreement"
                                        component={AcceptUserAgreementInput}
                                        componentProps={{
                                            label: text.AcceptUserAgreement,
                                            primary: true
                                        }}
                                    />
                                </Center>
                            </DigitDesign.CardBody>
                            <DigitDesign.CardButtons leftRight reverseDirection>
                                <CreateAccountButton
                                    submit
                                    text={text.CreateAccount}
                                    primary
                                    raised
                                />
                                <Spacing />
                            </DigitDesign.CardButtons>
                        </DigitDesign.Card>
                    )}
                />
            </Center>
        )}
    />
);

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
