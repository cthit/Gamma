import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitText,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./CreationOfAccountFinished.view.translations.json";

class CreationOfAccountFinished extends React.Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        return (
            <DigitTranslations
                translations={translations}
                render={text => (
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
                )}
            />
        );
    }
}

CreationOfAccountFinished.propTypes = {};

export default CreationOfAccountFinished;
