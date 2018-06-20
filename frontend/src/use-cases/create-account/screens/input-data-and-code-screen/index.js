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
  GammaCardSubTitle,
  Divider
} from "../../../../common-ui/design";
import Button from "styled-mdl/lib/components/buttons/Button";
import { Center, Spacing } from "../../../../common-ui/layout";

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

  constructor() {
    super();
  }

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
                  label="Kod"
                  onChange={e =>
                    this._handleInputChange(e.target.value, "code")
                  }
                />
              </Center>
              <Divider />
              <NickInput
                label="Nick"
                onChange={e =>
                  this._handleInputChange(e.target.value, "nick", true)
                }
              />
              <PasswordInput
                label="Ditt lösenord"
                onChange={e =>
                  this._handleInputChange(e.target.value, "password", true)
                }
              />
              <PasswordConfirmationInput
                label="Ditt lösenord igen"
                onChange={e =>
                  this._handleInputChange(
                    e.target.value,
                    "passwordConfirmation",
                    true
                  )
                }
              />
              <FirstnameInput
                label="Förstanamn"
                onChange={e =>
                  this._handleInputChange(e.target.value, "firstName", true)
                }
              />
              <LastnameInput
                label="Efternamn"
                onChange={e =>
                  this._handleInputChange(e.target.value, "lastName", true)
                }
              />
              <AttendanceYearInput
                label="Vilket år började du på IT?"
                onChange={e =>
                  this._handleInputChange(
                    e.target.value,
                    "attendanceYear",
                    true
                  )
                }
              />
              <AcceptUserAgreementInput
                label="Jag accepterar användaravtalet"
                onChange={e => {
                  this._handleInputChange(
                    !this.state.user.userAgreement,
                    "userAgreement",
                    true
                  );
                  e.stopPropagation(); //Otherwise it will be called twice
                }}
              />
            </Center>
          </GammaCardBody>
          <GammaCardButtons>
            <Button
              raisedAcceptUserAgreementInputAcceptUserAgreementInput
              primary
              onClick={() => this._handleSendDataAndCode()}
            >
              Skapa konto
            </Button>
            <Spacing />
          </GammaCardButtons>
        </GammaCard>
      </Center>
    );
  }

  _handleSendDataAndCode() {
    if (this._validate()) {
      this.props.sendDataAndCode(
        this.state.code,
        this._retrieveUserDataToSend()
      );
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
      this.state.user.password != "" &&
      this.state.user.password == this.state.user.passwordConfirmation
    );
  }

  _retrieveUserDataToSend() {
    return {
      nick: this.state.user.nick,
      password: this.state.user.password,
      firstName: this.state.user.firstName,
      lastName: this.state.user.lastName,
      attendanceYear: this.state.user.attendanceYear,
      userAgreement: this.state.user.userAgreement
    };
  }
}

export default InputDataAndCodeScreen;
