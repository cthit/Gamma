import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitText
} from "@cthit/react-digit-components";
import React from "react";

const Error = () => (
    <DigitLayout.Center>
        <DigitDesign.Card absWidth="300px" absHeight="300px">
            <DigitDesign.CardDisplayTitle text="Åh nej" />
            <DigitDesign.CardBody>
                <DigitText.Text text="Någonting gick snett när vi försökte prata med servern. Var vänligt och försök igen eller kontakta digit@chalmers.it" />
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons reverseDirection>
                <DigitButton text="Försök igen" />
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    </DigitLayout.Center>
);

export default Error;
