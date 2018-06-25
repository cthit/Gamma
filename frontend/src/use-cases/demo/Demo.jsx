import React from "react";
import {
  ButtonsContainer,
  CheckboxesContainer,
  IconButtonsContainer,
  MenusContainer,
  RadioButtonsContainer,
  SteppersContainer,
  SwitchesContainer,
  TextFieldsContainer,
  SelectsContainer
} from "./Demo.styles";

import { Add } from "@material-ui/icons";

import GammaButton from "../../common/views/gamma-button";
import GammaCheckbox from "../../common/views/gamma-checkbox";
import GammaIconButton from "../../common/views/gamma-icon-button";
import GammaMenu from "../../common/views/gamma-menu";
import GammaRadioButtonGroup from "../../common/views/gamma-radio-button-group";
import GammaStepper from "../../common/views/gamma-stepper";
import GammaSwitch from "../../common/views/gamma-switch";
import GammaTextField from "../../common/views/gamma-text-field";
import GammaSelect from "../../common/views/gamma-select";

class Demo extends React.Component {
  constructor(props) {
    super(props);

    this.gammaStepperRef = React.createRef();
  }

  render() {
    return (
      <div>
        <ButtonsContainer>
          <h2>Buttons</h2>
          <GammaButton
            primary
            raised
            text="This is a button"
            onClick={() => console.log("Button was clicked")}
          />
        </ButtonsContainer>
        <CheckboxesContainer>
          <h2>Checkboxes</h2>
          <GammaCheckbox
            secondary
            label="This is a checkbox"
            onChange={checked =>
              console.log("Checkbox is " + checked ? "checked" : "not checked")
            }
          />
        </CheckboxesContainer>
        <IconButtonsContainer>
          <h2>IconButtons</h2>
          <GammaIconButton
            onClick={() => console.log("Icon Button was clicked")}
          >
            <Add />
          </GammaIconButton>
        </IconButtonsContainer>
        <MenusContainer>
          <GammaMenu
            onClick={value => {
              console.log(value + " has been selected");
            }}
            valueToTextMap={{
              first_option: "First option",
              second_option: "Second option"
            }}
          />
        </MenusContainer>
        <RadioButtonsContainer>
          <GammaRadioButtonGroup
            onChange={value => {
              console.log(value);
            }}
            startValue="5000"
            upperLabel="Upperlabel"
            lowerLabel="Lowerlabel"
            radioButtonMap={{
              "5000": {
                primary: true,
                label: "Primary"
              },
              "4000": {
                secondary: true,
                label: "Secondary"
              },
              "3000": {
                label: "Default"
              },
              "2000": {
                disabled: true,
                label: "Disabled"
              }
            }}
          />
        </RadioButtonsContainer>
        <SteppersContainer>
          <GammaStepper
            ref={this.gammaStepperRef}
            steps={[
              {
                text: "1",
                element: <h2>asdf</h2>
              },
              {
                text: "Det andra steget",
                element: <h2>second</h2>
              },
              {
                text: "Step 3",
                element: <h3>asdffdas</h3>
              }
            ]}
            finishedElement={<h1>Done</h1>}
          />
          <GammaButton
            text="Förgående steg"
            onClick={() => {
              this.gammaStepperRef.current.previousStep();
            }}
          />
          <GammaButton
            text="Nästa steg"
            onClick={() => {
              this.gammaStepperRef.current.nextStep();
            }}
          />
        </SteppersContainer>
        <SwitchesContainer>
          <GammaSwitch
            primary
            startValue={true}
            label="1"
            onChange={value => {
              console.log("New  value: " + value);
            }}
          />
        </SwitchesContainer>
        <TextFieldsContainer>
          <GammaTextField
            onChange={newValue => {
              console.log("New value: " + newValue);
            }}
            startValue="Hej"
            upperLabel="Hello lowerLabel"
            lowerLabel="Why hello upperLabel"
          />
        </TextFieldsContainer>
        <SelectsContainer>
          <GammaSelect
            onChange={value => {
              console.log(value);
            }}
            upperLabel="Favorite Icecream flavor"
            lowerLabel="Choose the best flavor"
            valueToTextMap={{
              chocolate: "Chocolate",
              vanilla: "Vanilla",
              strawberry: "Strawberry"
            }}
            allowToChooseNone
          />
        </SelectsContainer>
      </div>
    );
  }
}

export default Demo;
