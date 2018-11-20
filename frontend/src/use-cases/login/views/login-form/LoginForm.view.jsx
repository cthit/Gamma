import React from "react";
import PropTypes from "prop-types";
import * as yup from "yup";

import {
    CIDInput,
    PasswordInput,
    LoginButton,
    CreateAccountButton,
    RememberMe
} from "./LoginForm.view.styles";

import GammaButton from "../../../../common/elements/gamma-button";
import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";

import { Center, MarginTop, Fill } from "../../../../common-ui/layout";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaCardTitle,
    GammaLink
} from "../../../../common-ui/design";

import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";

import translations from "./LoginForm.view.translations.json";

import { DigitTranslations } from "@cthit/react-digit-components";

const LoginForm = ({ login, toastOpen, redirectTo }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Login"
        render={text => (
            <Fill>
                {console.log(text)}
                <GammaForm
                    validationSchema={yup.object().shape({
                        cid: yup.string().required(text.FieldRequired),
                        password: yup.string().required(text.FieldRequired),
                        rememberMe: yup.boolean()
                    })}
                    initialValues={{ cid: "", password: "", rememberMe: false }}
                    onSubmit={(values, actions) => {
                        const data = {
                            cid: values["cid"],
                            password: values["password"]
                        };

                        login(data, values.rememberMe)
                            .then(response => {
                                redirectTo("/home");
                                toastOpen({
                                    text: text.SuccessfullLogin,
                                    duration: 3000
                                });
                            })
                            .catch(error => {
                                const code = statusCode(error);
                                const message = statusMessage(error);
                                switch (code) {
                                    case 422:
                                        switch (message) {
                                            case "INCORRECT_CID_OR_PASSWORD":
                                                toastOpen({
                                                    text:
                                                        text.IncorrectCidOrPassword,
                                                    duration: 3000
                                                });
                                        }
                                        break;
                                    default:
                                        toastOpen({
                                            text: text.SomethingWentWrong,
                                            duration: 3000
                                        });
                                }
                            });

                        actions.resetForm();
                    }}
                    render={({ errors, touched }) => (
                        <GammaCard
                            absWidth="300px"
                            absHeight="300px"
                            hasSubTitle
                        >
                            <GammaCardTitle text={text.Login} />
                            <GammaCardBody>
                                <Center>
                                    <GammaFormField
                                        name="cid"
                                        component={CIDInput}
                                        componentProps={{
                                            upperLabel: text.EnterYourCid
                                        }}
                                    />
                                    <GammaFormField
                                        name="password"
                                        component={PasswordInput}
                                        componentProps={{
                                            upperLabel: text.EnterYourPassword,
                                            password: true
                                        }}
                                    />

                                    <GammaFormField
                                        name="rememberMe"
                                        component={RememberMe}
                                        componentProps={{
                                            label: text.RememberMe,
                                            primary: true
                                        }}
                                    />
                                </Center>
                            </GammaCardBody>
                            <GammaCardButtons reverseDirection>
                                <LoginButton
                                    text={text.Login}
                                    primary
                                    raised
                                    submit
                                />
                                <GammaLink to="/create-account">
                                    <CreateAccountButton
                                        text={text.CreateAccount}
                                    />
                                </GammaLink>
                            </GammaCardButtons>
                        </GammaCard>
                    )}
                />
            </Fill>
        )}
    />
);

export default LoginForm;
