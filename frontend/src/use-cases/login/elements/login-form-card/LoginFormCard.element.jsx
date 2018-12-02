import React from "react";
import { NavLink } from "react-router-dom";
import styled from "styled-components";

import {
    DigitFormField,
    DigitDesign,
    DigitLayout,
    DigitText,
    DigitButton,
    DigitTextField,
    DigitSwitch
} from "@cthit/react-digit-components";

const Link = styled(NavLink)`
    color: inherit;
    text-decoration: none;
    padding: 0px;
`;

const LoginFormCard = ({ text }) => (
    <DigitDesign.Card
        minWidth="300px"
        maxWidth="500px"
        minHeight="300px"
        maxHeight="500px"
        hasSubTitle
    >
        <DigitDesign.CardBody>
            <DigitLayout.Center>
                <DigitText.Heading5 text={text.LoginIntoYourITAccount} />
            </DigitLayout.Center>
            <DigitLayout.Padding />
            <DigitLayout.Center>
                <DigitLayout.Column>
                    <DigitLayout.Size width="300px">
                        <DigitFormField
                            name="cid"
                            component={DigitTextField}
                            componentProps={{
                                upperLabel: text.EnterYourCid,
                                filled: true
                            }}
                        />
                    </DigitLayout.Size>
                    <DigitLayout.Size width="300px">
                        <DigitFormField
                            name="password"
                            component={DigitTextField}
                            componentProps={{
                                upperLabel: text.EnterYourPassword,
                                password: true,
                                filled: true
                            }}
                        />
                    </DigitLayout.Size>

                    <DigitFormField
                        name="rememberMe"
                        component={DigitSwitch}
                        componentProps={{
                            label: text.RememberMe,
                            primary: true
                        }}
                    />
                </DigitLayout.Column>
            </DigitLayout.Center>
        </DigitDesign.CardBody>
        <DigitDesign.CardButtons>
            <DigitLayout.Fill>
                <DigitLayout.Row justifyContent="space-between">
                    <Link to="/create-account">
                        <DigitButton text={text.CreateAccount} />
                    </Link>
                    <DigitButton text={text.Login} primary raised submit />
                </DigitLayout.Row>
            </DigitLayout.Fill>
        </DigitDesign.CardButtons>
    </DigitDesign.Card>
);

export default LoginFormCard;
