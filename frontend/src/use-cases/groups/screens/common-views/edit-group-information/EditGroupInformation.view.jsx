import React from "react";

import * as yup from "yup";

import {
  GammaCard,
  GammaCardBody,
  GammaCardButtons
} from "../../../../../common-ui/design";

import GammaForm from "../../../../../common/elements/gamma-form";
import GammaFormField from "../../../../../common/elements/gamma-form-field";
import GammaTextField from "../../../../../common/elements/gamma-text-field";
import GammaButton from "../../../../../common/elements/gamma-button";
import GammaSelect from "../../../../../common/elements/gamma-select";

const EditGroupInformation = ({ initialValues, onSubmit }) => (
  <GammaForm
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
    render={() => (
      <GammaCard>
        <GammaCardBody>
          <GammaFormField
            name="name"
            component={GammaTextField}
            componentProps={{ upperLabel: "Name" }}
          />

          <GammaFormField
            name="description.sv"
            component={GammaTextField}
            componentProps={{ upperLabel: "Description Sv" }}
          />

          <GammaFormField
            name="description.en"
            component={GammaTextField}
            componentProps={{ upperLabel: "Description En" }}
          />

          <GammaFormField
            name="email"
            component={GammaTextField}
            componentProps={{ upperLabel: "Email" }}
          />

          <GammaFormField
            name="func.sv"
            component={GammaTextField}
            componentProps={{ upperLabel: "Function Sv" }}
          />

          <GammaFormField
            name="func.en"
            component={GammaTextField}
            componentProps={{ upperLabel: "Function En" }}
          />

          <GammaFormField
            name="groupType"
            component={GammaSelect}
            componentProps={{
              valueToTextMap: {
                SOCIETY: "Förening",
                COMMITTEE: "Kommittée",
                BOARD: "Board"
              },
              upperLabel: "Type"
            }}
          />

          <GammaCardButtons reverseDirection>
            <GammaButton text="Create group" submit raised primary />
          </GammaCardButtons>
        </GammaCardBody>
      </GammaCard>
    )}
  />
);

export default EditGroupInformation;
