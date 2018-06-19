import React, { Component } from "react";

import { Padding, MarginTop, Center } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaCardButtons
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
        <Center>
          <GammaCard absWidth="300px" absHeight="300px">
            <GammaCardTitle>Börja skapa ditt IT-konto</GammaCardTitle>
            <GammaCardBody>
              <Center>
                <CIDTextfield
                  onChange={e =>
                    this.setState({
                      ...this.state,
                      cid: e.target.value
                    })
                  }
                  label="CID"
                />
              </Center>
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
            </GammaCardButtons>
          </GammaCard>
        </Center>
      </MarginTop>
    );
  }
}

export default InputCidScreen;
