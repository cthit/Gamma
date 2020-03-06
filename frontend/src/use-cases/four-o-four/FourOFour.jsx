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
        <DigitLayout.Center>
            <DigitDesign.Card absWidth="300px">
                <DigitDesign.CardTitle text={"404 - " + text.PageNotFound} />
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
        </DigitLayout.Center>
    );
};
export default FourOFour;
