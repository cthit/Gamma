import React from "react";

import {
    DigitDesign,
    DigitLayout,
    DigitText,
    DigitTranslations
} from "@cthit/react-digit-components";

import FourOFourTranslations from "./FourOFour.translations.json";

class FourOFour extends React.Component {
    constructor(props) {
        super(props);

        props.gammaLoadingFinished();
    }

    render() {
        return (
            <DigitTranslations
                uniquePath="FourOFour"
                translations={FourOFourTranslations}
                render={text => (
                    <DigitLayout.Center>
                        <DigitDesign.Card absWidth="300px">
                            <DigitDesign.CardTitle
                                text={"404 - " + text.PageNotFound}
                            />
                            <DigitDesign.CardHeaderImage src="/jedimind.jpg" />
                            <DigitDesign.CardBody>
                                <DigitText.Text
                                    text={
                                        "This is not the site you're looking for! " +
                                        text.ContactDigit
                                    }
                                />
                            </DigitDesign.CardBody>
                        </DigitDesign.Card>
                    </DigitLayout.Center>
                )}
            />
        );
    }
}
export default FourOFour;
