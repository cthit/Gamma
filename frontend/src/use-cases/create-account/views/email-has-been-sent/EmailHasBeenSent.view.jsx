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
    return (
      <Center>
        <GammaCard minWidth="300px" maxWidth="500px">
          <GammaCardTitle text={this.props.text.AnEmailShouldBeSent} />
          <GammaCardBody>
            <Text text={this.props.text.AnEmailShouldBeSentDescription} />
          </GammaCardBody>
          <GammaCardButtons leftRight reverseDirection>
            <ButtonNavLink to="/create-account/input">
              <GammaButton
                primary
                raised
                onClick={() => {}}
                text={this.props.text.HaveRecievedACode}
              />
            </ButtonNavLink>
            <ButtonNavLink to="/create-account">
              <GammaButton
                raised
                onClick={() => {}}
                text={this.props.text.Back}
              />
            </ButtonNavLink>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }
}

export default EmailHasBeenSent;
