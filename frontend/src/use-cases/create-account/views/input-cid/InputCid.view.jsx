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
import GammaButton from "../../../../common/elements/gamma-button";

import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";

import * as yup from "yup";

class InputCid extends Component {
  render() {
    //Functions
    const { sendCid } = this.props;

    //Texts
    const {
      EnterYourCid,
      EnterYourCidDescription,
      SendCid,
      Cid,
      FieldRequired
    } = this.props.text;

    return (
      <MarginTop>
        <Center>
          <GammaForm
            validationSchema={yup.object().shape({
              cid: yup.string().required(FieldRequired)
            })}
            initialValues={{ cid: "" }}
            onSubmit={(values, actions) => {
              actions.resetForm();
              sendCid(values);
            }}
            render={({ errors, touched }) => (
              <GammaCard absWidth="300px" absHeight="300px" hasSubTitle>
                <GammaCardTitle text={EnterYourCid} />
                <GammaCardSubTitle text={EnterYourCidDescription} />
                <GammaCardBody>
                  <Center>
                    <GammaFormField
                      name="cid"
                      component={CIDInput}
                      componentProps={{
                        upperLabel: Cid
                      }}
                    />
                  </Center>
                </GammaCardBody>
                <GammaCardButtons reverseDirection>
                  <GammaButton text={SendCid} primary raised submit />
                </GammaCardButtons>
              </GammaCard>
            )}
          />
        </Center>
      </MarginTop>
    );
  }
}

export default InputCid;
