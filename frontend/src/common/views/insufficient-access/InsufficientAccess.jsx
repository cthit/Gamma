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
            <DigitDesign.Card absWidth="300px">
                <DigitDesign.CardTitle text={text.InsufficientAccess} />
                <DigitDesign.CardHeaderImage src="/theoffice-no-admin.gif" />
                <DigitDesign.CardBody>
                    <DigitText.Text text={text.YouDontHaveAccess} />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

export default InsufficientAccess;
