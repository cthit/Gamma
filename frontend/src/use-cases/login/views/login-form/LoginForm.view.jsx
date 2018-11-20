import { DigitTranslations } from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaCardTitle,
    GammaLink
} from "../../../../common-ui/design";
import { Center, Fill } from "../../../../common-ui/layout";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import {
    CIDInput,
    CreateAccountButton,
    LoginButton,
    PasswordInput,
    RememberMe
} from "./LoginForm.view.styles";
import translations from "./LoginForm.view.translations.json";

import { DigitForm, DigitFormField } from "@cthit/react-digit-components";

const LoginForm = ({ login, toastOpen, redirectTo }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Login"
        render={text => (
            <Fill>
                {console.log(text)}
                <DigitForm
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
                                    <DigitFormField
                                        name="cid"
                                        component={CIDInput}
                                        componentProps={{
                                            upperLabel: text.EnterYourCid
                                        }}
                                    />
                                    <DigitFormField
                                        name="password"
                                        component={PasswordInput}
                                        componentProps={{
                                            upperLabel: text.EnterYourPassword,
                                            password: true
                                        }}
                                    />

                                    <DigitFormField
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
