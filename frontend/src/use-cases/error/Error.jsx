import React from "react";

import { Center } from "../../common-ui/layout";
import { Display, Text } from "../../common-ui/text";
import GammaButton from "../../common/elements/gamma-button";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardDisplayTitle
} from "../../common-ui/design";

const Error = ({}) => (
  <Center>
    <GammaCard absWidth="300px" absHeight="300px">
      <GammaCardDisplayTitle text="Åh nej" />
      <GammaCardBody>
        <Text text="Någonting gick snett när vi försökte prata med servern. Var vänligt och försök igen eller kontakta digit@chalmers.it" />
      </GammaCardBody>
      <GammaCardButtons reverseDirection>
        <GammaButton text="Försök igen" />
      </GammaCardButtons>
    </GammaCard>
  </Center>
);

export default Error;
