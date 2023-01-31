import React, { useState } from "react";

import {
    DigitDesign,
    DigitButton,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./FiveZeroZero.element.translations";

const FiveZeroZero = ({ getMe, reset }) => {
    const [text] = useDigitTranslations(translations);
    const [tryAgainButtonDisabled, setTryAgainButtonDisabled] = useState(false);

    return (
        <DigitDesign.Card
            margin={{ left: "auto", right: "auto" }}
            alignSelf="center"
            size={{ minWidth: "300px", maxWidth: "400px", height: "250px" }}
        >
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={text.BackendDownTitle} />
            </DigitDesign.CardHeader>
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
