import {
    DigitLayout,
    useDigitTranslations,
    useDigitToast,
    DigitEditDataCard,
    DigitSelect
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import { DigitSwitch, DigitTextField } from "@cthit/react-digit-components";
import translations from "./InputDataAndCode.view.translations.json";
import { useHistory } from "react-router";
import { createAccount } from "../../../../api/create-account/post.createAccount.api";

const InputDataAndCode = () => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const history = useHistory();

    return (
        <DigitLayout.Center>
            <DigitEditDataCard
                titleText={text.CompleteCreation}
                subtitleText={text.CompleteCreationDescription}
                submitText={text.CreateAccount}
                extraButton={{
                    text: text.Back
                }}
                extraButtonTo={"/create-account/email-sent"}
                onSubmit={(values, actions) => {
                    const cid = values.cid;
                    const user = {
                        whitelist: {
                            cid: cid
                        },
                        ...values
                    };
                    createAccount(user)
                        .then(() => {
                            actions.resetForm();
                            history.push("/create-account/finished");
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
                                        default:
                                            errorMessage =
                                                text.SomethingWentWrong;
                                    }
                                    break;
                                default:
                                    errorMessage = text.SomethingWentWrong;
                            }
                            queueToast({
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
                    email: "",
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
                    email: yup.string()
                        .required(text.FieldRequired)
                        .email(text.NotEmail)
                        .matches(/(^((?!@student.chalmers.se).)*$)/, text.NonStudentEmailError),
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
                        .oneOf([yup.ref("password")], text.PasswordsDoNotMatch)
                        .required(text.FieldRequired),
                    userAgreement: yup
                        .boolean()
                        .oneOf([true])
                        .required(text.FieldRequired)
                })}
                minWidth={"300px"}
                maxWidth={"600px"}
                keysOrder={[
                    "cid",
                    "code",
                    "password",
                    "passwordConfirmation",
                    "nick",
                    "firstName",
                    "lastName",
                    "email",
                    "acceptanceYear",
                    "userAgreement"
                ]}
                keysComponentData={{
                    cid: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.YourCid,
                            outlined: true,
                            maxLength: 12
                        }
                    },
                    code: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.CodeFromYourStudentEmail,
                            outlined: true,
                            maxLength: 15
                        }
                    },
                    nick: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Nick,
                            outlined: true,
                            maxLength: 20
                        }
                    },
                    password: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Password,
                            outlined: true,
                            password: true
                        }
                    },
                    passwordConfirmation: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.ConfirmPassword,
                            outlined: true,
                            password: true
                        }
                    },
                    firstName: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.FirstName,
                            outlined: true,
                            maxLength: 15
                        }
                    },
                    lastName: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.LastName,
                            outlined: true,
                            maxLength: 15
                        }
                    },
                    email: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.NonStudentEmail,
                            outlined: true,
                            maxLength: 100
                        }
                    },
                    acceptanceYear: {
                        component: DigitSelect,
                        componentProps: {
                            valueToTextMap: _generateAcceptanceYears(),
                            upperLabel: text.WhichYearDidYouStart,
                            reverse: true,
                            outlined: true
                        }
                    },
                    userAgreement: {
                        component: DigitSwitch,
                        componentProps: {
                            label: text.AcceptUserAgreement,
                            primary: true
                        }
                    }
                }}
            />
        </DigitLayout.Center>
    );
};

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

export default InputDataAndCode;
