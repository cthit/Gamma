import React from "react";

import { Center } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardTitle
} from "../../../../common-ui/design";

import { ButtonNavLink } from "./styles";
import { GammaButton } from "../../../../common/gui/gamma-button";
import { Text } from "../../../../common-ui/text";

class EmailHasBeenSentScreen extends React.Component {
  render() {
    return (
      <Center>
        <GammaCard minWidth="300px" maxWidth="500px">
          <GammaCardTitle>
            Ett mail har nu skickats till din studentmail
          </GammaCardTitle>
          <GammaCardBody>
            <Text>
              Vi har nu lyckats skicka ett mail till din studentmail. Du behöver
              nu gå in på den mailen och kopiera koden. Den koden behöver du när
              du skapar ditt konto i nästa steg.
            </Text>
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <ButtonNavLink to="/create-account/input">
              <GammaButton primary raised onClick={() => {}} text="Gå vidare" />
            </ButtonNavLink>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }
}

export default EmailHasBeenSentScreen;
