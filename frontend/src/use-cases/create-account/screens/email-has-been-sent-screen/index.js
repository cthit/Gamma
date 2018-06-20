import React from "react";

import { Button } from "styled-mdl";
import { Center } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardTitle
} from "../../../../common-ui/design";

import { ButtonNavLink } from "./styles";

class EmailHasBeenSentScreen extends React.Component {
  render() {
    return (
      <Center>
        <GammaCard minWidth="300px" maxWidth="500px">
          <GammaCardTitle>
            Ett mail har nu skickats till din studentmail
          </GammaCardTitle>
          <GammaCardBody>
            Vi har nu lyckats skicka ett mail till din studentmail. Du behöver
            nu gå in på den mailen och kopiera koden. Den koden behöver du när
            du skapar ditt konto i nästa steg.
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <ButtonNavLink to="/create-account/input">
              <Button raised primary>
                Gå vidare
              </Button>
            </ButtonNavLink>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }
}

export default EmailHasBeenSentScreen;
