import React from "react";

import {
  ConfirmationCodeInput,
  NickInput,
  PasswordInput,
  PasswordConfirmationInput,
  FirstnameInput,
  LastnameInput,
  AttendanceYearInput
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
    nick: "",
    firstName: "",
    lastName: "",
    attendanceYear: ""
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
                    this._handleTextfieldChange(e.target.value, "code")
                  }
                />
              </Center>
              <Divider />
              <NickInput
                label="Nick"
                onChange={e =>
                  this._handleTextfieldChange(e.target.value, "nick")
                }
              />
              <PasswordInput
                label="Ditt lösenord"
                onChange={e =>
                  this._handleTextfieldChange(e.target.value, "password")
                }
              />
              <PasswordConfirmationInput
                label="Ditt lösenord igen"
                onChange={e =>
                  this._handleTextfieldChange(
                    e.target.value,
                    "passwordConfirmation"
                  )
                }
              />
              <FirstnameInput
                label="Förstanamn"
                onChange={e =>
                  this._handleTextfieldChange(e.target.value, "firstName")
                }
              />
              <LastnameInput
                label="Efternamn"
                onChange={e =>
                  this._handleTextfieldChange(e.target.value, "lastName")
                }
              />
              <AttendanceYearInput
                label="Vilket år började du på IT?"
                onChange={e =>
                  this._handleTextfieldChange(e.target.value, "attendanceYear")
                }
              />
            </Center>
          </GammaCardBody>
          <GammaCardButtons>
            <Button
              raised
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
    this.props.sendDataAndCode(this.state);
  }

  _handleTextfieldChange(newValue, prop) {
    const newState = this.state;
    newState[prop] = newValue;
    this.setState(newState);
  }
}

export default InputDataAndCodeScreen;
