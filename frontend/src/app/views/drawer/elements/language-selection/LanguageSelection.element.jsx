import React, { useEffect, useState } from "react";
import useGammaUser from "../../../../../common/hooks/use-gamma-user/useGammaUser";
import { editMe } from "../../../../../api/me/put.me.api";
import {
    DigitSelect,
    useDigitToast,
    useDigitTranslations
} from "@cthit/react-digit-components";
import translations from "./LanguageSelection.element.translations";

const LanguageSelection = () => {
    const user = useGammaUser();
    const [text, , setActiveLanguage] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const [language, setLanguage] = useState(user.language);
    const [initialRender, setInitialRender] = useState(true);

    useEffect(() => {
        setActiveLanguage(language);
        if (!initialRender) {
            queueToast({ text: text.LanguageUpdated });
        }
        setInitialRender(false);
    }, [language]);

    return (
        <DigitSelect
            alignSelf={"center"}
            value={language}
            onChange={e => {
                editMe({ ...user, language: e.target.value })
                    .then(() => {
                        setLanguage(e.target.value);
                    })
                    .catch(error => {
                        console.log(error);
                    });
            }} //update me
            valueToTextMap={{ sv: text.Swedish, en: text.English }}
            outlined
            upperLabel={text.YourLanguage}
        />
    );
};

export default LanguageSelection;
