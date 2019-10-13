import React, { useEffect } from "react";
import {
    DigitDesign,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./InsufficientAccess.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../../app/views/gamma-loading/GammaLoading.view.action-creator";

const InsufficientAccess = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

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
