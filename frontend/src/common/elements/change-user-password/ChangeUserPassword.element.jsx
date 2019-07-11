import React from "react";
import {
    DigitTranslations,
    DigitLayout,
    DigitDesign,
    DigitFormField,
    DigitTextField,
    DigitForm,
    DigitButton,
    DigitIfElseRendering,
} from "@cthit/react-digit-components";
import * as yup from "yup";
import translations from "./ChangeUserPassword.element.translations";

const ChangeUserPassword = ({ user, onSubmit, renderOldPassword }) => (
    <DigitTranslations
        translations={translations}
        render={(text) => (
            <DigitLayout.MarginTop>
                <DigitLayout.Center>
                    <DigitForm
                        validationSchema={yup.object().shape({
                            oldPassword: yup
                                .string(),
                            password: yup
                                .string()
                                .min(8, text.MinimumLength)
                                .required(text.FieldRequired),
                            confirmNewPassword: yup
                                .string()
                                .oneOf([yup.ref("password")],
                                    text.PasswordDoNotMatch)
                                .required(text.FieldRequired),
                        })}
                        initialValues={{
                            oldPassword: "",
                            password: "",
                            confirmNewPassword: "",
                        }}
                        onSubmit={onSubmit}
                        render={({errors, touched}) => (
                            <DigitDesign.Card
                                absWidth="300px"
                                absHeight="370px"
                            >
                                <DigitDesign.CardTitle
                                    text={text.ChangePasswordOn + user}
                                />
                                <DigitDesign.CardBody>
                                    <DigitLayout.Center>
                                        <DigitIfElseRendering
                                            test={renderOldPassword}
                                            ifRender={() => (
                                                <DigitFormField
                                                    name="oldPassword"
                                                    component={DigitTextField}
                                                    componentProps={{
                                                        upperLabel: text.OldPassword,
                                                        password: true,
                                                        outlined: true,
                                                    }}
                                                />
                                            )}
                                        />

                                        <DigitFormField
                                            name="password"
                                            component={DigitTextField}
                                            componentProps={{
                                                upperLabel: text.NewPassword,
                                                password: true,
                                                outlined: true,
                                            }}
                                        />
                                        <DigitFormField
                                            name="confirmNewPassword"
                                            component={DigitTextField}
                                            componentProps={{
                                                upperLabel: text.ConfirmNewPassword,
                                                password: true,
                                                outlined: true,
                                            }}
                                        />
                                    </DigitLayout.Center>
                                </DigitDesign.CardBody>
                                <DigitDesign.CardButtons
                                    reverseDirection
                                >
                                    <DigitButton
                                        text={text.ChangePassword}
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
        )
        }
    />
);

export default ChangeUserPassword;