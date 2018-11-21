import {
    DigitButton,
    DigitDesign,
    DigitText,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import { Center } from "../../../../common-ui/layout";
import { ButtonNavLink } from "./EmailHasBeenSent.view.styles";
import translations from "./EmailHasBeenSent.view.translations.json";

const EmailHasBeenSent = ({}) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <Center>
                <DigitDesign.Card minWidth="300px" maxWidth="500px">
                    <DigitDesign.CardTitle text={text.AnEmailShouldBeSent} />
                    <DigitDesign.CardBody>
                        <DigitText.Text
                            text={text.AnEmailShouldBeSentDescription}
                        />
                    </DigitDesign.CardBody>
                    <DigitDesign.CardButtons leftRight reverseDirection>
                        <ButtonNavLink to="/create-account/input">
                            <DigitButton
                                primary
                                raised
                                onClick={() => {}}
                                text={text.HaveRecievedACode}
                            />
                        </ButtonNavLink>
                        <ButtonNavLink to="/create-account">
                            <DigitButton
                                raised
                                onClick={() => {}}
                                text={text.Back}
                            />
                        </ButtonNavLink>
                    </DigitDesign.CardButtons>
                </DigitDesign.Card>
            </Center>
        )}
    />
);

EmailHasBeenSent.propTypes = {};

export default EmailHasBeenSent;
