import React from "react";
import {
  AcceptUserAgreementInput,
  AttendanceYearInput,
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

import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";

import * as yup from "yup";

class InputDataAndCode extends React.Component {
  /*state = {
    errors: {
      cid: false,
      code: false,
      nick: false,
      password: false,
      passwordConfirmation: false,
      firstName: false,
      lastName: false,
      userAgreement: false
    },
    user: {
      code: "",
      whitelist: {
        cid: ""
      },
      nick: "",
      firstName: "",
      lastName: "",
      attendanceYear: this._getCurrentYear(),
      password: "",
      passwordConfirmation: "",
      userAgreement: false
    }
  };*/

  render() {
    //Functions
    const { sendDataAndCode } = this.props;

    //Texts
    const {
      CompleteCreation,
      CompleteCreationDescription,
      YourCid,
      CodeFromYourStudentEmail,
      Nick,
      Password,
      ConfirmPassword,
      FirstName,
      LastName,
      WhichYearDidYouStart,
      AcceptUserAgreement,
      CreateAccount,

      PasswordsDoNotMatch,
      FieldRequired
    } = this.props.text;

    return (
      <Center>
        <GammaForm
          onSubmit={(values, actions) => {
            const cid = values.cid;
            delete values.cid;
            const user = {
              whitelist: {
                cid: cid
              },
              ...values
            };
            sendDataAndCode(user);
            actions.resetForm();
          }}
          initialValues={{
            cid: "",
            code: "",
            nick: "",
            firstName: "",
            lastName: "",
            attendanceYear: "2018",
            password: "",
            passwordConfirmation: "",
            userAgreement: false
          }}
          validationSchema={yup.object().shape({
            cid: yup.string().required(FieldRequired),
            code: yup.string().required(FieldRequired),
            nick: yup.string().required(FieldRequired),
            firstName: yup.string().required(FieldRequired),
            lastName: yup.string().required(FieldRequired),
            attendanceYear: yup
              .number()
              .min(2001)
              .max(this._getCurrentYear())
              .required(FieldRequired),
            password: yup.string().required(FieldRequired),
            passwordConfirmation: yup
              .string()
              .oneOf([yup.ref("password")], PasswordsDoNotMatch)
              .required(FieldRequired),
            userAgreement: yup
              .boolean()
              .oneOf([true])
              .required(FieldRequired)
          })}
          render={props => (
            <GammaCard minWidth="300px" maxWidth="600px" hasSubTitle>
              <GammaCardTitle text={CompleteCreation} />
              <GammaCardSubTitle text={CompleteCreationDescription} />
              <GammaCardBody>
                <Center>
                  <GammaFormField
                    name="cid"
                    component={ConfirmCidInput}
                    componentProps={{
                      upperLabel: YourCid
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="code"
                    component={ConfirmationCodeInput}
                    componentProps={{
                      upperLabel: CodeFromYourStudentEmail
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="nick"
                    component={NickInput}
                    componentProps={{
                      upperLabel: Nick
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="password"
                    component={PasswordInput}
                    componentProps={{
                      upperLabel: Password,
                      password: true
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="passwordConfirmation"
                    component={PasswordConfirmationInput}
                    componentProps={{
                      upperLabel: ConfirmPassword,
                      password: true
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="firstName"
                    component={FirstnameInput}
                    componentProps={{
                      upperLabel: FirstName
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="lastName"
                    component={LastnameInput}
                    componentProps={{
                      upperLabel: LastName
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="attendanceYear"
                    component={AttendanceYearInput}
                    componentProps={{
                      valueToTextMap: this._generateAttendanceYears(),
                      upperLabel: WhichYearDidYouStart,
                      reverse: true
                    }}
                  />
                  <Spacing />
                  <GammaFormField
                    name="userAgreement"
                    component={AcceptUserAgreementInput}
                    componentProps={{
                      label: AcceptUserAgreement,
                      primary: true
                    }}
                  />
                </Center>
              </GammaCardBody>
              <GammaCardButtons leftRight reverseDirection>
                <CreateAccountButton
                  submit
                  text={CreateAccount}
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
  }

  _getCurrentYear() {
    return new Date().getFullYear() + "";
  }

  _generateAttendanceYears() {
    const output = {};
    const startYear = 2001;
    const currentYear = this._getCurrentYear();
    for (var i = currentYear; i >= startYear; i--) {
      output[i] = i + "";
    }
    return output;
  }
}
export default InputDataAndCode;
