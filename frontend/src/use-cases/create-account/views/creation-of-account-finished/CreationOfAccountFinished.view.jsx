import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./CreationOfAccountFinished.view.translations.json";

const CreationOfAccountFinished = ({}) => {
    const [text] = useDigitTranslations(translations);

    return (
        <DigitLayout.Center>
            <DigitDesign.Card>
                <DigitDesign.CardTitle text={text.CongratsTitle} />
                <DigitDesign.CardHeaderImage src="/theofficeparty.gif" />
                <DigitDesign.CardBody>
                    <DigitLayout.Center>
                        <DigitText.Text text={text.CongratsBody} />
                    </DigitLayout.Center>
                </DigitDesign.CardBody>
                <DigitDesign.CardButtons reverseDirection>
                    <DigitDesign.Link to="/login">
                        <DigitButton
                            raised
                            primary
                            text={text.LoginForTheFirstTime}
                        />
                    </DigitDesign.Link>
                </DigitDesign.CardButtons>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

CreationOfAccountFinished.propTypes = {};

export default CreationOfAccountFinished;
