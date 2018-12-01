import React from "react";

import {
    CIDInput,
    CreateAccountButton,
    LoginButton,
    PasswordInput,
    RememberMe
} from "./LoginFormCard.element.styles";

import {
    DigitFormField,
    DigitDesign,
    DigitLayout
} from "@cthit/react-digit-components";

const LoginFormCard = ({ text }) => (
    <DigitDesign.Card absWidth="300px" absHeight="300px" hasSubTitle>
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
            <LoginButton text={text.Login} primary raised submit />
            <DigitDesign.Link to="/create-account">
                <CreateAccountButton text={text.CreateAccount} />
            </DigitDesign.Link>
        </DigitDesign.CardButtons>
    </DigitDesign.Card>
);

export default LoginFormCard;
