import React from "react";

import {
    DigitDesign,
    DigitSelect,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./ChangeLanguageLocally.view.translations";

const ChangeLanguageLocally = () => {
    const [text, activeLanguage, setActiveLanguage] =
        useDigitTranslations(translations);

    return (
        <DigitDesign.Card margin={{ bottom: "16px" }}>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={text.ChooseLanguage} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                <DigitSelect
                    alignSelf={"center"}
                    value={activeLanguage}
                    onChange={e => {
                        setActiveLanguage(e.target.value);
                    }}
                    valueToTextMap={{ sv: text.Swedish, en: text.English }}
                    outlined
                    upperLabel={text.YourLanguage}
                />
            </DigitDesign.CardBody>
        </DigitDesign.Card>
    );
};

export default ChangeLanguageLocally;
