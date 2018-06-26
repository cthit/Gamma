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

import GammaButton from "../../common/elements/gamma-button";
import GammaCheckbox from "../../common/elements/gamma-checkbox";
import GammaIconButton from "../../common/views/gamma-icon-button";
import GammaMenu from "../../common/views/gamma-menu";
import GammaRadioButtonGroup from "../../common/elements/gamma-radio-button-group";
import GammaStepper from "../../common/views/gamma-stepper";
import GammaSwitch from "../../common/elements/gamma-switch";
import GammaTextField from "../../common/elements/gamma-text-field";
import GammaSelect from "../../common/elements/gamma-select";

import { Formik, Field, Form } from "formik";

import LoginForm from "./elements/login-form";

class Demo extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return <LoginForm />;
  }
}

export default Demo;
