import React from "react";

import { Center, Spacing } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardTitle
} from "../../../../common-ui/design";

import { ButtonNavLink } from "./styles";
import { GammaButton } from "../../../../common/gui/gamma-button";
import { Text } from "../../../../common-ui/text";

class EmailHasBeenSentView extends React.Component {
  render() {
    return (
      <Center>
        <GammaCard minWidth="300px" maxWidth="500px">
          <GammaCardTitle>
            Ett mail ska ha skickats till din studentmail
          </GammaCardTitle>
          <GammaCardBody>
            <Text>
              Om du inte får ett mail på några minuter kan du ha råkat skriva
              fel cid. Om du är säker att du skriver rätt med fortfarande inte
              får ett mail kan det antingen bero på att mailet har hamnat i
              skräppost, eller så är du inte inlagd i digITs system. I det
              senare fallet, var vänligen och skicka ett mail till
              digit@chalmers.it.
            </Text>
          </GammaCardBody>
          <GammaCardButtons leftRight reverseDirection>
            <ButtonNavLink to="/create-account/input">
              <GammaButton
                primary
                raised
                onClick={() => {}}
                text="Jag har fått en kod"
              />
            </ButtonNavLink>
            <ButtonNavLink to="/create-account">
              <GammaButton raised onClick={() => {}} text="Tillbaka" />
            </ButtonNavLink>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }
}

export default EmailHasBeenSentView;
