import React from "react";
import {
    DigitDesign,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./InsufficientAccess.translations";

const InsufficientAccess = () => {
    const [text] = useDigitTranslations(translations);

    return (
        <DigitLayout.Center>
            <DigitDesign.Card size={{ width: "300px" }}>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.InsufficientAccess} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardHeaderImage src="/403.gif" />
                <DigitDesign.CardBody>
                    <DigitText.Text text={text.YouDontHaveAccess} />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

export default InsufficientAccess;
