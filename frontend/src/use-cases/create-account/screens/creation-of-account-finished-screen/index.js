import React from "react";
import { Center } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaCardButtons
} from "../../../../common-ui/design";
import { Text } from "../../../../common-ui/text";

import { GammaButton } from "../../../../common/gui/gamma-button";

class CreationOfAccountFinishedScreen extends React.Component {
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

export default CreationOfAccountFinishedScreen;
