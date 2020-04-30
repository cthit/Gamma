import React from "react";

import {
    DigitButton,
    DigitDesign,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { useHistory } from "react-router-dom";

import translations from "./FourOFour.translations.json";

const FourOFour = () => {
    const [text] = useDigitTranslations(translations);
    const history = useHistory();

    return (
        <DigitDesign.Card
            size={{ width: "300px", height: "450px" }}
            margin={{ left: "auto", right: "auto" }}
            alignSelf={"center"}
        >
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={"404 - " + text.PageNotFound} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardHeaderImage src="/jedimind.jpg" />
            <DigitDesign.CardBody>
                <DigitText.Text
                    text={
                        "This is not the site you're looking for! " +
                        text.ContactDigit
                    }
                />
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons reverseDirection>
                <DigitButton
                    raised
                    primary
                    text={text.TakeMeHome}
                    onClick={() => history.push("/")}
                />
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};
export default FourOFour;
