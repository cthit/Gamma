import React from "react";

import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { getBackendUrl } from "common/utils/configs/envVariablesLoader";

import translations from "./CreationOfAccountFinished.view.translations.json";

const CreationOfAccountFinished = () => {
    const [text] = useDigitTranslations(translations);

    return (
        <DigitLayout.Center>
            <DigitDesign.Card>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.CongratsTitle} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    <DigitLayout.Center>
                        <DigitText.Text text={text.CongratsBody} />
                    </DigitLayout.Center>
                </DigitDesign.CardBody>
                <DigitDesign.CardButtons reverseDirection>
                    <DigitButton
                        raised
                        primary
                        text={text.LoginForTheFirstTime}
                        onClick={() => {
                            window.location.href = getBackendUrl() + "/login";
                        }}
                    />
                </DigitDesign.CardButtons>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

CreationOfAccountFinished.propTypes = {};

export default CreationOfAccountFinished;
