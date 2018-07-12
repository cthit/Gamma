import React from "react";
import { Add } from "@material-ui/icons";

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

import LoginForm from "./elements/login-form";

import GammaButton from "../../common/elements/gamma-button";
import GammaCheckbox from "../../common/elements/gamma-checkbox";
import GammaIconButton from "../../common/elements/gamma-icon-button";
import GammaMenu from "../../common/views/gamma-menu";
import GammaRadioButtonGroup from "../../common/elements/gamma-radio-button-group";
import GammaStepper from "../../common/elements/gamma-stepper";
import GammaSwitch from "../../common/elements/gamma-switch";
import GammaTextField from "../../common/elements/gamma-text-field";
import GammaSelect from "../../common/elements/gamma-select";

import { Heading, Title, Subtitle } from "../../common-ui/text";

class Demo extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <Heading text="Hej" />
        <Title text="Det här är en title" />
        <Subtitle text="Det här är en subtitle" />
        <LoginForm />
      </div>
    );
  }
}

export default Demo;
