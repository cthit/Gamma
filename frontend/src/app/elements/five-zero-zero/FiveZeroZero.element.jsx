import React, { useState } from "react";
import translations from "./FiveZeroZero.element.translations";
import {
    DigitDesign,
    DigitButton,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

const FiveZeroZero = ({ getMe }) => {
    const [text] = useDigitTranslations(translations);
    const [tryAgainButtonDisabled, setTryAgainButtonDisabled] = useState(false);

    return (
        <DigitDesign.Card
            margin={{ left: "auto", right: "auto" }}
            alignSelf="center"
            size={{ width: "300px", height: "450px" }}
        >
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={text.BackendDownTitle} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardHeaderImage src="/nope.gif" />
            <DigitDesign.CardBody>
                <DigitText.Text text={text.BackendDown} />
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons reverseDirection>
                <DigitButton
                    disabled={tryAgainButtonDisabled}
                    text={text.TryAgain}
                    primary
                    raised
                    onClick={() => {
                        setTryAgainButtonDisabled(true);
                        getMe()
                            .then(() => setTryAgainButtonDisabled(false))
                            .catch(() => setTryAgainButtonDisabled(false));
                    }}
                />
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};

export default FiveZeroZero;
