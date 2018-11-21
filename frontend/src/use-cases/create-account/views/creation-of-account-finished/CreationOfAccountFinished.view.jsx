import React from "react";
import PropTypes from "prop-types";

import { Center } from "../../../../common-ui/layout";
import {
    DigitTranslations,
    DigitText,
    DigitDesign
} from "@cthit/react-digit-components";
import translations from "./CreationOfAccountFinished.view.translations.json";

import { DigitButton } from "@cthit/react-digit-components";

const CreationOfAccountFinished = ({}) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <Center>
                <DigitDesign.Card>
                    <DigitDesign.CardTitle text={text.CongratsTitle} />
                    <DigitDesign.CardBody>
                        <Center>
                            <DigitText.Text text={text.CongratsBody} />
                        </Center>
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
            </Center>
        )}
    />
);

CreationOfAccountFinished.propTypes = {};

export default CreationOfAccountFinished;
