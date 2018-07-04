import React from "react";
import PropTypes from "prop-types";

import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardTitle
} from "../../../../common-ui/design";
import { Center } from "../../../../common-ui/layout";
import GammaButton from "../../../../common/elements/gamma-button";
import { Text } from "../../../../common-ui/text";

const CreationOfAccountFinished = ({ text }) => (
  <Center>
    <GammaCard>
      <GammaCardTitle text={text.CongratsTitle} />
      <GammaCardBody>
        <Center>
          <Text text={text.CongratsBody} />
        </Center>
      </GammaCardBody>
      <GammaCardButtons reverseDirection>
        <GammaButton
          raised
          primary
          onClick={() => {}}
          text={text.LoginForTheFirstTime}
        />
      </GammaCardButtons>
    </GammaCard>
  </Center>
);

CreationOfAccountFinished.propTypes = {
  text: PropTypes.object.isRequired
};

export default CreationOfAccountFinished;
