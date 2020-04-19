import {
    DigitButton,
    DigitDesign,
    DigitText,
    DigitLayout,
    useDigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./EmailHasBeenSent.view.translations.json";

const EmailHasBeenSent = () => {
    const [text] = useDigitTranslations(translations);

    return (
        <DigitLayout.Center>
            <DigitDesign.Card size={{ minWidth: "300px", maxWidth: "600px" }}>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.AnEmailShouldBeSent} />
                </DigitDesign.CardHeader>
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
                            text={text.HaveReceivedACode}
                        />
                    </DigitDesign.Link>
                    <DigitDesign.Link to="/create-account">
                        <DigitButton
                            onClick={() => {}}
                            text={text.IHaveNotReceivedACode}
                        />
                    </DigitDesign.Link>
                </DigitDesign.CardButtons>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

EmailHasBeenSent.propTypes = {};

export default EmailHasBeenSent;
