import {
    DigitForm,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";

import translations from "./LoginForm.element.translations.json";
import LoginFormCard from "../login-form-card/LoginFormCard.element";

import { INCORRECT_CID_OR_PASSWORD } from "../../../../api/login/errors.login.api";

import { CID, PASSWORD } from "../../../../api/login/props.login.api";

function generateValidationSchema(text) {
    const schema = {};

    schema[CID] = yup.string().required(text.FieldRequired);
    schema[PASSWORD] = yup.string().required(text.FieldRequired);
    schema["passwordMe"] = yup.boolean();

    return yup.object().shape(schema);
}

function onLogin(
    login,
    data,
    resetForm,
    resetPasswordField,
    toastOpen,
    redirectTo,
    text
) {
    const loginData = {
        cid: data[CID],
        password: data[PASSWORD]
    };

    login(loginData, data.rememberMe)
        .then(() => {
            redirectTo("/home");
            toastOpen({
                text: text.SuccessfullLogin,
                duration: 3000
            });
            resetForm();
        })
        .catch(error => {
            if (INCORRECT_CID_OR_PASSWORD.equals(error)) {
                toastOpen({
                    text: text.IncorrectCidOrPassword,
                    duration: 3000
                });
            } else {
                toastOpen({
                    text: text.SomethingWentWrong,
                    duration: 6000
                });
            }

            resetPasswordField();
        });
}

const LoginForm = ({ login, toastOpen, redirectTo }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Login"
        render={text => (
            <DigitForm
                validationSchema={generateValidationSchema(text)}
                initialValues={{ cid: "", password: "", rememberMe: false }}
                onSubmit={(values, actions) => {
                    onLogin(
                        login,
                        values,
                        actions.resetForm,
                        () => {
                            actions.setFieldValue(PASSWORD, "", false);
                        },
                        toastOpen,
                        redirectTo,
                        text
                    );
                }}
                render={() => <LoginFormCard text={text} />}
            />
        )}
    />
);

export default LoginForm;
