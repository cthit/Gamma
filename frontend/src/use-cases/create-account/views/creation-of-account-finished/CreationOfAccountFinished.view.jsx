import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitText,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./CreationOfAccountFinished.view.translations.json";

const CreationOfAccountFinished = ({}) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <DigitLayout.Center>
                <DigitDesign.Card>
                    <DigitDesign.CardTitle text={text.CongratsTitle} />
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
        )}
    />
);

CreationOfAccountFinished.propTypes = {};

export default CreationOfAccountFinished;
