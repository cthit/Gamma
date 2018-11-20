import React from "react";
import PropTypes from "prop-types";

import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaCardTitle,
    GammaLink
} from "../../../../common-ui/design";
import { Center } from "../../../../common-ui/layout";
import { Text } from "../../../../common-ui/text";
import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./CreationOfAccountFinished.view.translations.json";

import { DigitButton } from "@cthit/react-digit-components";

const CreationOfAccountFinished = ({}) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <Center>
                <GammaCard>
                    <GammaCardTitle text={text.CongratsTitle} />
                    <GammaCardBody>
                        <Center>
                            <Text text={text.CongratsBody} />
                        </Center>
                    </GammaCardBody>
                    <GammaCardButtons reverseDirection>
                        <GammaLink to="/login">
                            <DigitButton
                                raised
                                primary
                                text={text.LoginForTheFirstTime}
                            />
                        </GammaLink>
                    </GammaCardButtons>
                </GammaCard>
            </Center>
        )}
    />
);

CreationOfAccountFinished.propTypes = {};

export default CreationOfAccountFinished;
