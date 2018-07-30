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
import GammaButton from "../../../../common/elements/gamma-button";
import { Text } from "../../../../common-ui/text";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./CreationOfAccountFinished.view.translations.json";

const CreationOfAccountFinished = ({}) => (
  <GammaTranslations
    translations={translations}
    uniquePath="CreateAccount.View.CreationOfAccountFinished"
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
              <GammaButton raised primary text={text.LoginForTheFirstTime} />
            </GammaLink>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    )}
  />
);

CreationOfAccountFinished.propTypes = {};

export default CreationOfAccountFinished;
