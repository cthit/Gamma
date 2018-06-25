import React from "react";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardTitle
} from "../../../../common-ui/design";

import { Center } from "../../../../common-ui/layout";
import GammaButton from "../../../../common/views/gamma-button";
import { Text } from "../../../../common-ui/text";

class CreationOfAccountFinished extends React.Component {
  render() {
    return (
      <Center>
        <GammaCard>
          <GammaCardTitle>Grattis! Du har skapat ett IT-konto</GammaCardTitle>
          <GammaCardBody>
            <Center>
              <Text>
                Du har nu skapat ett konto hos digIT. Nu kan du använda digITs
                tjänster.
              </Text>
            </Center>
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <GammaButton
              raised
              primary
              onClick={() => {}}
              text="Logga in för första gången"
            />
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }
}

export default CreationOfAccountFinished;
