import {
    DigitDesign,
    DigitForm,
    DigitFormField,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
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

const LoginForm = ({ login, toastOpen, redirectTo }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Login"
        render={text => (
            <DigitLayout.Fill>
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
                        <DigitDesign.Card
                            absWidth="300px"
                            absHeight="300px"
                            hasSubTitle
                        >
                            <DigitDesign.CardTitle text={text.Login} />
                            <DigitDesign.CardBody>
                                <DigitLayout.Center>
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
                                </DigitLayout.Center>
                            </DigitDesign.CardBody>
                            <DigitDesign.CardButtons reverseDirection>
                                <LoginButton
                                    text={text.Login}
                                    primary
                                    raised
                                    submit
                                />
                                <DigitDesign.Link to="/create-account">
                                    <CreateAccountButton
                                        text={text.CreateAccount}
                                    />
                                </DigitDesign.Link>
                            </DigitDesign.CardButtons>
                        </DigitDesign.Card>
                    )}
                />
            </DigitLayout.Fill>
        )}
    />
);

export default LoginForm;
