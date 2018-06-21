import React, { Component } from "react";

import { MarginTop, Center, Spacing } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaCardButtons,
  GammaCardSubTitle
} from "../../../../common-ui/design";

import { CIDInput } from "./styles";
import { GammaButton } from "../../../../common/gui/gamma-button";
import { GammaIconButton } from "../../../../common/gui/gamma-icon-buitton";
import { GammaFabButton } from "../../../../common/gui/gamma-fab-button";

import { Add } from "@material-ui/icons";
import { Temp } from "./temp";

class InputCidScreen extends Component {
  state = {
    cid: ""
  };

  render() {
    return (
      <MarginTop>
        <Center>
          <GammaCard absWidth="300px" absHeight="300px" hasSubTitle>
            <GammaCardTitle>Skriv in ditt CID</GammaCardTitle>
            <GammaCardSubTitle>
              Vi kommer skicka ett mail till din studentmail för att bekräfta
              din identitet.
            </GammaCardSubTitle>
            <GammaCardBody>
              <Center>
                <CIDInput
                  startValue="hej"
                  onChange={value =>
                    this.setState({
                      ...this.state,
                      cid: value
                    })
                  }
                  upperLabel="CID"
                />
              </Center>
            </GammaCardBody>
            <GammaCardButtons reverseDirection>
              <GammaButton
                text="Skicka CID"
                onClick={() => {
                  this.props.sendCid(this.state.cid);
                }}
                primary
                raised
              />
            </GammaCardButtons>
          </GammaCard>
          <Spacing />
          <div>
            <Temp />
          </div>
        </Center>
      </MarginTop>
    );
  }
}

export default InputCidScreen;
