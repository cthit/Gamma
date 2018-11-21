import React from "react";

import { Center } from "../../common-ui/layout";

import {
    DigitButton,
    DigitText,
    DigitDesign
} from "@cthit/react-digit-components";

const Error = ({}) => (
    <Center>
        <DigitDesign.Card absWidth="300px" absHeight="300px">
            <DigitDesign.CardDisplayTitle text="Åh nej" />
            <DigitDesign.CardBody>
                <DigitText.Text text="Någonting gick snett när vi försökte prata med servern. Var vänligt och försök igen eller kontakta digit@chalmers.it" />
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons reverseDirection>
                <DigitButton text="Försök igen" />
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    </Center>
);

export default Error;
