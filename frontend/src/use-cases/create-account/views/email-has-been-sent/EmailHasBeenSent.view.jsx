import {
    DigitButton,
    DigitDesign,
    DigitText,
    DigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./EmailHasBeenSent.view.translations.json";

const EmailHasBeenSent = () => (
    <DigitTranslations
        translations={translations}
        render={text => (
            <DigitLayout.Center>
                <DigitDesign.Card minWidth="300px" maxWidth="500px">
                    <DigitDesign.CardTitle text={text.AnEmailShouldBeSent} />
                    <DigitDesign.CardBody>
                        <DigitText.Text
                            text={text.AnEmailShouldBeSentDescription}
                        />
                    </DigitDesign.CardBody>
                    <DigitDesign.CardButtons leftRight reverseDirection>
                        <DigitDesign.Link to="/create-account/input">
                            <DigitButton
                                primary
                                raised
                                onClick={() => {}}
                                text={text.HaveRecievedACode}
                            />
                        </DigitDesign.Link>
                        <DigitDesign.Link to="/create-account">
                            <DigitButton
                                raised
                                onClick={() => {}}
                                text={text.Back}
                            />
                        </DigitDesign.Link>
                    </DigitDesign.CardButtons>
                </DigitDesign.Card>
            </DigitLayout.Center>
        )}
    />
);

EmailHasBeenSent.propTypes = {};

export default EmailHasBeenSent;
