import React from "react";
import PropTypes from "prop-types";

import { ButtonNavLink } from "./EmailHasBeenSent.view.styles";

import { Center } from "../../../../common-ui/layout";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaCardTitle
} from "../../../../common-ui/design";
import { Text } from "../../../../common-ui/text";

import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./EmailHasBeenSent.view.translations.json";

import { DigitButton } from "@cthit/react-digit-components";

const EmailHasBeenSent = ({}) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <Center>
                <GammaCard minWidth="300px" maxWidth="500px">
                    <GammaCardTitle text={text.AnEmailShouldBeSent} />
                    <GammaCardBody>
                        <Text text={text.AnEmailShouldBeSentDescription} />
                    </GammaCardBody>
                    <GammaCardButtons leftRight reverseDirection>
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
                    </GammaCardButtons>
                </GammaCard>
            </Center>
        )}
    />
);

EmailHasBeenSent.propTypes = {};

export default EmailHasBeenSent;
