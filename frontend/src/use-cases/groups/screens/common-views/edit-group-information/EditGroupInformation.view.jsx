import React from "react";

import * as yup from "yup";

import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons
} from "../../../../../common-ui/design";

import GammaEditData from "../../../../../common/elements/gamma-edit-data";
import GammaTextField from "../../../../../common/elements/gamma-text-field";
import GammaButton from "../../../../../common/elements/gamma-button";
import GammaSelect from "../../../../../common/elements/gamma-select";

const EditGroupInformation = ({ initialValues, onSubmit }) => (
  <GammaEditData
    titleText="Grupp"
    submitText="Spara"
    validationSchema={yup.object().shape({
      name: yup.string().required(),
      description: yup
        .object()
        .shape({
          sv: yup.string().required(),
          en: yup.string().required()
        })
        .required(),
      email: yup.string().required(),
      func: yup
        .object()
        .shape({
          sv: yup.string().required(),
          en: yup.string().required()
        })
        .required(),
      groupType: yup.string().required()
    })}
    onSubmit={onSubmit}
    initialValues={initialValues}
    keysOrder={[
      "name",
      "description.sv",
      "description.en",
      "email",
      "func.sv",
      "func.en",
      "groupType"
    ]}
    keysComponentData={{
      name: {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Name"
        }
      },
      "description.sv": {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Description SV"
        }
      },
      "description.en": {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Description EN"
        }
      },
      email: {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Email"
        }
      },
      "func.sv": {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Function SV"
        }
      },
      "func.en": {
        component: GammaTextField,
        componentProps: {
          upperLabel: "Function EN"
        }
      },
      groupType: {
        component: GammaSelect,
        componentProps: {
          upperLabel: "Group Type",
          valueToTextMap: {
            SOCIETY: "Förening",
            COMMITTEE: "Kommittée",
            BOARD: "Board"
          }
        }
      }
    }}
  />
);

export default EditGroupInformation;
