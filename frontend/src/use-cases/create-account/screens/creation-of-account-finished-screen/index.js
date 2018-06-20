import React from "react";
import { Center } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaCardButtons
} from "../../../../common-ui/design";
import { Button } from "styled-mdl/lib/components/buttons";

class CreationOfAccountFinishedScreen extends React.Component {
  render() {
    return (
      <Center>
        <GammaCard>
          <GammaCardTitle>Grattis! Du har skapat ett IT-konto</GammaCardTitle>
          <GammaCardBody>
            <Center>
              Du har nu skapat ett konto hos digIT. Nu kan du använda digITs
              tjänster.
            </Center>
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <Button primary raised>
              Logga in för första gången
            </Button>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }
}

export default CreationOfAccountFinishedScreen;
