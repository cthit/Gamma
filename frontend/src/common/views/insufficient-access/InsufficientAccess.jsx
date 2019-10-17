import React, { useEffect } from "react";
import {
    DigitDesign,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./InsufficientAccess.translations";
import { useDispatch, useSelector } from "react-redux";
import { deltaLoadingFinished } from "../../../app/views/delta-loading/DeltaLoading.view.action-creator";

const InsufficientAccess = () => {
    const userLoaded = useSelector(state => state.user.loaded);
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();

    useEffect(() => {
        if (userLoaded) {
            dispatch(deltaLoadingFinished());
        }
    }, [userLoaded]);

    if (!userLoaded) {
        return null;
    }

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
