import React from "react";
import { Center } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardTitle
} from "../../../../common-ui/design";

import { ButtonNavLink } from "./EmailHasBeenSent.view.styles";
import GammaButton from "../../../../common/elements/gamma-button";
import { Text } from "../../../../common-ui/text";

class EmailHasBeenSent extends React.Component {
  render() {
    //Texts
    const {
      AnEmailShouldBeSent,
      AnEmailShouldBeSentDescription,
      HaveRecievedACode,
      Back
    } = this.props.text;

    return (
      <Center>
        <GammaCard minWidth="300px" maxWidth="500px">
          <GammaCardTitle text={AnEmailShouldBeSent} />
          <GammaCardBody>
            <Text text={AnEmailShouldBeSentDescription} />
          </GammaCardBody>
          <GammaCardButtons leftRight reverseDirection>
            <ButtonNavLink to="/create-account/input">
              <GammaButton
                primary
                raised
                onClick={() => {}}
                text={HaveRecievedACode}
              />
            </ButtonNavLink>
            <ButtonNavLink to="/create-account">
              <GammaButton raised onClick={() => {}} text={Back} />
            </ButtonNavLink>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }
}

export default EmailHasBeenSent;
