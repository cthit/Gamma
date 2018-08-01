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
import GammaTranslations from "../../../../../common/declaratives/gamma-translations";

import translations from "./GroupForm.view.translations";

const GroupForm = ({ initialValues, onSubmit }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Groups.Screen.GroupForm"
    render={text => (
      <GammaEditData
        titleText={text.Group}
        submitText={text.SaveGroup}
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
          type: yup.string().required()
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
          "type"
        ]}
        keysComponentData={{
          name: {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.Name
            }
          },
          "description.sv": {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.DescriptionSv
            }
          },
          "description.en": {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.DescriptionEn
            }
          },
          email: {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.Email
            }
          },
          "func.sv": {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.FunctionSv
            }
          },
          "func.en": {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.FunctionEn
            }
          },
          type: {
            component: GammaSelect,
            componentProps: {
              upperLabel: text.Type,
              valueToTextMap: {
                SOCIETY: text.Society,
                COMMITTEE: text.Committee,
                BOARD: text.Board
              }
            }
          }
        }}
      />
    )}
  />
);

export default GroupForm;
