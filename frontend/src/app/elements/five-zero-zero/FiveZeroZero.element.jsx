import React, { useState } from "react";
import translations from "./FiveZeroZero.element.translations";
import {
    DigitDesign,
    DigitButton,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

const FiveZeroZero = ({ getMe, reset, error }) => {
    const [text] = useDigitTranslations(translations);
    const [tryAgainButtonDisabled, setTryAgainButtonDisabled] = useState(false);

    if (error != null) {
        console.log(error);
    }

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
                        if (getMe != null) {
                            getMe()
                                .then(() => setTryAgainButtonDisabled(false))
                                .catch(() => setTryAgainButtonDisabled(false));
                        } else if (reset != null) {
                            reset();
                        } else {
                            window.location.reload();
                        }
                    }}
                />
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};

export default FiveZeroZero;
