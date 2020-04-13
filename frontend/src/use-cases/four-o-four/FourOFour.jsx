import React from "react";

import {
    DigitDesign,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./FourOFour.translations.json";

const FourOFour = () => {
    const [text] = useDigitTranslations(translations);
    return (
        <DigitDesign.Card
            size={{ width: "300px" }}
            margin={{ left: "auto", right: "auto" }}
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
        </DigitDesign.Card>
    );
};
export default FourOFour;
