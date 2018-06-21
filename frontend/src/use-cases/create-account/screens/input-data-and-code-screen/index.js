import React from "react";

import {
  ConfirmationCodeInput,
  NickInput,
  PasswordInput,
  PasswordConfirmationInput,
  FirstnameInput,
  LastnameInput,
  AttendanceYearInput,
  AcceptUserAgreementInput
} from "./styles";

import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaCardButtons,
  GammaCardSubTitle
} from "../../../../common-ui/design";
import { Center, Spacing } from "../../../../common-ui/layout";
import { GammaButton } from "../../../../common/gui/gamma-button";

class InputDataAndCodeScreen extends React.Component {
  state = {
    code: "",
    user: {
      nick: "",
      firstName: "",
      lastName: "",
      attendanceYear: "",
      password: "",
      passwordConfirmation: "",
      userAgreement: false
    }
  };

  render() {
    return (
      <Center>
        <GammaCard maxWidth="600px" minWidth="300px" hasSubTitle>
          <GammaCardTitle>Slutför registreringen av konto</GammaCardTitle>
          <GammaCardSubTitle>
            Skriv in koden du fick på din skolmail samt följande information
          </GammaCardSubTitle>
          <GammaCardBody>
            <Center>
              <Center>
                <ConfirmationCodeInput
                  upperLabel="Kod"
                  maxLength={10}
                  lowerLabelReflectLength
                  onChange={value => this._handleInputChange(value, "code")}
                />
              </Center>
              <Spacing />
              <NickInput
                upperLabel="Nick"
                maxLength={20}
                lowerLabelReflectLength
                onChange={value => this._handleInputChange(value, "nick", true)}
              />
              <Spacing />
              <PasswordInput
                upperLabel="Ditt lösenord"
                password
                onChange={value =>
                  this._handleInputChange(value, "password", true)
                }
              />
              <Spacing />
              <PasswordConfirmationInput
                upperLabel="Ditt lösenord igen"
                password
                onChange={value =>
                  this._handleInputChange(value, "passwordConfirmation", true)
                }
              />
              <Spacing />
              <FirstnameInput
                upperLabel="Förstanamn"
                maxLength={20}
                lowerLabelReflectLength
                onChange={value =>
                  this._handleInputChange(value, "firstName", true)
                }
              />
              <Spacing />
              <LastnameInput
                upperLabel="Efternamn"
                maxLength={20}
                lowerLabelReflectLength
                onChange={value =>
                  this._handleInputChange(value, "lastName", true)
                }
              />
              <Spacing />
              <AttendanceYearInput
                upperLabel="Vilket år började du på IT?"
                startValue={"" + this._getCurrentYear()}
                valueToTextMap={this._generateAttendanceYears()}
                onChange={value =>
                  this._handleInputChange(value, "attendanceYear", true)
                }
              />
              <Spacing />
              <AcceptUserAgreementInput
                primary
                label="Jag accepterar användaravtalet"
                onChange={checked =>
                  this.setState({
                    ...this.state,
                    checked: checked
                  })
                }
              />
            </Center>
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <GammaButton
              onClick={() => this._handleSendDataAndCode()}
              text="Skapa konto"
              primary
              raised
            />
            <Spacing />
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }

  _getCurrentYear() {
    return new Date().getFullYear();
  }

  _generateAttendanceYears() {
    const output = {};
    const startYear = 2001;
    const currentYear = this._getCurrentYear();
    for (var i = startYear; i <= currentYear; i++) {
      output[i] = i;
    }
    return output;
  }

  _handleSendDataAndCode() {
    if (this._validate()) {
      this.props.sendDataAndCode(this._retrieveUserDataToSend());
    }
  }

  _handleInputChange(newValue, prop, isUserData) {
    const newState = this.state;
    if (isUserData) {
      newState.user[prop] = newValue;
    } else {
      newState[prop] = newValue;
    }
    this.setState(newState);
  }

  _validate() {
    return this._validatePassword();
  }

  _validatePassword() {
    return (
      this.state.user.password !== "" &&
      this.state.user.password === this.state.user.passwordConfirmation
    );
  }

  _retrieveUserDataToSend() {
    return {
      nick: this.state.user.nick,
      password: this.state.user.password,
      firstName: this.state.user.firstName,
      lastName: this.state.user.lastName,
      attendanceYear: this.state.user.attendanceYear,
      userAgreement: this.state.user.userAgreement,
      code: this.state.code
    };
  }
}

export default InputDataAndCodeScreen;
