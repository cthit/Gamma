import { Center, MarginTop } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardSubTitle,
  GammaCardTitle
} from "../../../../common-ui/design";
import React, { Component } from "react";

import { CIDInput } from "./InputCid.view.styles";
import GammaButton from "../../../../common/views/gamma-button";

class InputCid extends Component {
  state = {
    cid: ""
  };

  constructor() {
    super();

    this.cidInputRef = React.createRef();
  }

  render() {
    return (
      <MarginTop>
        <Center>
          <GammaCard absWidth="300px" absHeight="300px" hasSubTitle>
            <GammaCardTitle>{this.props.text.EnterYourCid}</GammaCardTitle>
            <GammaCardSubTitle>
              {this.props.text.EnterYourCidDescription}
            </GammaCardSubTitle>
            <GammaCardBody>
              <Center>
                <CIDInput
                  innerRef={this.cidInputRef}
                  validate={value => {
                    return value.length > 0;
                  }}
                  maxLength={10}
                  lowerLabelReflectLength
                  onChange={value =>
                    this.setState({
                      ...this.state,
                      cid: value
                    })
                  }
                  upperLabel={this.props.text.Cid}
                />
              </Center>
            </GammaCardBody>
            <GammaCardButtons reverseDirection>
              <GammaButton
                text={this.props.text.SendCid}
                onClick={() => {
                  if (this.state.cid.length === 0) {
                    this.cidInputRef.current.invalidate();
                    this.props.showError(
                      "Du har inte skrivit in något cid ännu"
                    );
                  } else {
                    this.props.sendCid(this.state.cid);
                  }
                }}
                primary
                raised
              />
            </GammaCardButtons>
          </GammaCard>
        </Center>
      </MarginTop>
    );
  }
}

export default InputCid;
