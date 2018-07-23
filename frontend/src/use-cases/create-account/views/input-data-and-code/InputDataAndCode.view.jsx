import React from "react";
import PropTypes from "prop-types";
import * as yup from "yup";

import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";

import {
  AcceptUserAgreementInput,
  AcceptanceYearInput,
  ConfirmCidInput,
  ConfirmationCodeInput,
  CreateAccountButton,
  FirstnameInput,
  LastnameInput,
  NickInput,
  PasswordConfirmationInput,
  PasswordInput
} from "./InputDataAndCode.view.styles";

import { Center, Spacing } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons,
  GammaCardSubTitle,
  GammaCardTitle
} from "../../../../common-ui/design";

const InputDataAndCode = ({ sendDataAndCode, text }) => (
  <Center>
    <GammaForm
      onSubmit={(values, actions) => {
        const cid = values.cid;
        const user = {
          whitelist: {
            cid: cid
          },
          ...values
        };
        sendDataAndCode(
          user,
          {
            TOO_SHORT_PASSWORD: text.TOO_SHORT_PASSWORD,
            CODE_OR_CID_IS_WRONG: text.CODE_OR_CID_IS_WRONG,
            SomethingWentWrong: text.SomethingWentWrong
          },
          actions.resetForm
        );
      }}
      initialValues={{
        cid: "",
        code: "",
        nick: "",
        firstName: "",
        lastName: "",
        acceptanceYear: "",
        password: "",
        passwordConfirmation: "",
        userAgreement: false
      }}
      validationSchema={yup.object().shape({
        cid: yup.string().required(text.FieldRequired),
        code: yup.string().required(text.FieldRequired),
        nick: yup.string().required(text.FieldRequired),
        firstName: yup.string().required(text.FieldRequired),
        lastName: yup.string().required(text.FieldRequired),
        acceptanceYear: yup
          .number()
          .min(2001)
          .max(_getCurrentYear())
          .required(text.FieldRequired),
        password: yup
          .string()
          .min(8, text.MinimumLength)
          .required(text.FieldRequired),
        passwordConfirmation: yup
          .string()
          .oneOf([yup.ref("password")], text.PasswordsDoNotMatch)
          .required(text.FieldRequired),
        userAgreement: yup
          .boolean()
          .oneOf([true])
          .required(text.FieldRequired)
      })}
      render={props => (
        <GammaCard minWidth="300px" maxWidth="600px" hasSubTitle>
          <GammaCardTitle text={text.CompleteCreation} />
          <GammaCardSubTitle text={text.CompleteCreationDescription} />
          <GammaCardBody>
            <Center>
              <GammaFormField
                name="cid"
                component={ConfirmCidInput}
                componentProps={{
                  upperLabel: text.YourCid
                }}
              />
              <Spacing />
              <GammaFormField
                name="code"
                component={ConfirmationCodeInput}
                componentProps={{
                  upperLabel: text.CodeFromYourStudentEmail
                }}
              />
              <Spacing />
              <GammaFormField
                name="nick"
                component={NickInput}
                componentProps={{
                  upperLabel: text.Nick
                }}
              />
              <Spacing />
              <GammaFormField
                name="password"
                component={PasswordInput}
                componentProps={{
                  upperLabel: text.Password,
                  password: true
                }}
              />
              <Spacing />
              <GammaFormField
                name="passwordConfirmation"
                component={PasswordConfirmationInput}
                componentProps={{
                  upperLabel: text.ConfirmPassword,
                  password: true
                }}
              />
              <Spacing />
              <GammaFormField
                name="firstName"
                component={FirstnameInput}
                componentProps={{
                  upperLabel: text.FirstName
                }}
              />
              <Spacing />
              <GammaFormField
                name="lastName"
                component={LastnameInput}
                componentProps={{
                  upperLabel: text.LastName
                }}
              />
              <Spacing />
              <GammaFormField
                name="acceptanceYear"
                component={AcceptanceYearInput}
                componentProps={{
                  valueToTextMap: _generateAcceptanceYears(),
                  upperLabel: text.WhichYearDidYouStart,
                  reverse: true
                }}
              />
              <Spacing />
              <GammaFormField
                name="userAgreement"
                component={AcceptUserAgreementInput}
                componentProps={{
                  label: text.AcceptUserAgreement,
                  primary: true
                }}
              />
            </Center>
          </GammaCardBody>
          <GammaCardButtons leftRight reverseDirection>
            <CreateAccountButton
              submit
              text={text.CreateAccount}
              primary
              raised
            />
            <Spacing />
          </GammaCardButtons>
        </GammaCard>
      )}
    />
  </Center>
);

function _getCurrentYear() {
  return new Date().getFullYear() + "";
}

function _generateAcceptanceYears() {
  const output = {};
  const startYear = 2001;
  const currentYear = _getCurrentYear();
  for (var i = currentYear; i >= startYear; i--) {
    output[i] = i + "";
  }
  return output;
}

InputDataAndCode.propTypes = {
  sendDataAndCode: PropTypes.func.isRequired,
  text: PropTypes.object.isRequired
};

export default InputDataAndCode;
