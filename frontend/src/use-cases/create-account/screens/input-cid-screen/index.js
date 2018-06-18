import React, { Component } from "react";

import { Padding, MarginTop } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaCardButtons,
  AbsoluteCenter
} from "../../../../common-ui/design";
import { HeadingLevel5 } from "../../../../common-ui/text";

import { Button } from "styled-mdl";

import { CIDTextfield } from "./styles";

class InputCidScreen extends Component {
  state = {
    cid: "asdf"
  };

  render() {
    return (
      <MarginTop>
        <AbsoluteCenter>
          <GammaCard absWidth="300px" absHeight="300px">
            <GammaCardTitle>Börja skapa ditt IT-konto</GammaCardTitle>
            <GammaCardBody>
              <CIDTextfield
                onChange={e =>
                  this.setState({
                    ...this.state,
                    cid: e.target.value
                  })
                }
                label="CID"
              />
            </GammaCardBody>
            <GammaCardButtons>
              <Button
                onClick={() => {
                  console.log(this.state.cid);
                  this.props.sendCid(this.state.cid);
                }}
                colored
              >
                Skicka cid
              </Button>
              <Button>Å nej</Button>
            </GammaCardButtons>
          </GammaCard>
        </AbsoluteCenter>
      </MarginTop>
    );
  }
}

export default InputCidScreen;
