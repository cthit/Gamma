import React from "react";
import {
    DigitTranslations,
    DigitLayout,
    DigitDesign,
    DigitFormField,
    DigitTextField,
} from "@cthit/react-digit-components";
import * as yup from "yup";
import translations from "ChangeUserPassword.element.translations";

const ChangeUserPassword = ({ user, onSubmit }) => (
    <DigitTranslations
        translations={translations}
        render={(text) => (
            <DigitLayout.MarginTop>
                <DigitLayout.Center>
                    <DigitForm
                        validationSchema={yup.object.shape({
                            oldPassword: yup
                                .string()
                                .min(8, text.MinimumLength)
                                .required(text.FieldRequired),
                            newPassword: yup
                                .string()
                                .min(8, text.MinimumLength)
                                .required(text.FieldRequired),
                            confirmNewPassword: yup
                                .string()
                                .oneOf([yup.ref("newPassword")],
                                    text.PasswordDoNotMatch)
                                .required(text.FieldRequired),
                        })}
                        initialValues={{
                            oldPassword: "",
                            newPassword: "",
                            confirmNewPassword: "",
                        }}
                        onSubmit={onSubmit}
                        render={({erros, touched}) => (
                            <DigitDesign.Card
                                absWidth="400px"
                                absHeight="400px"
                            >
                                <DigitDesign.CardTitle
                                    text={text.ChangePassword + user}
                                />
                                <DigitDesign.CardBody>
                                    <DigitLayout.Center>
                                        <DigitFormField
                                            name="oldPassword"
                                            component={DigitTextField}
                                            componentProps={{
                                                upperLabel: text.OldPassword,
                                                password: true,
                                                outlined: true,
                                            }}
                                        />
                                        <DigitFormField
                                            name="newPassword"
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