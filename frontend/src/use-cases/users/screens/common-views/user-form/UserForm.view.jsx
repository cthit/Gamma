import React from "react";
import * as yup from "yup";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardButtons,
  GammaCardBody
} from "../../../../../common-ui/design";
import GammaButton from "../../../../../common/elements/gamma-button";
import GammaTextField from "../../../../../common/elements/gamma-text-field";
import GammaSelect from "../../../../../common/elements/gamma-select";
import GammaEditData from "../../../../../common/elements/gamma-edit-data";
import GammaTranslations from "../../../../../common/declaratives/gamma-translations";
import EditWebsites from "../../../../../common/views/edit-websites";
import translations from "./UserForm.view.translations.json";

function _getCurrentYear() {
  return new Date().getFullYear() + "";
}

function _generateAcceptanceYears() {
  const output = {};
  const startYear = 2001;
  const currentYear = _getCurrentYear();
  for (var i = currentYear; i >= startYear; i--) {
    output[i] = i + "";
  }

  return output;
}

const UserForm = ({
  initialValues,
  onSubmit,
  titleText,
  submitText,
  availableWebsites
}) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Users.Screen.CommonViews.UserForm"
    render={text => (
      <GammaEditData
        titleText={titleText}
        submitText={submitText}
        initialValues={initialValues}
        onSubmit={(values, actions) => {
          onSubmit(values, actions);
        }}
        validationSchema={yup.object().shape({
          firstName: yup.string().required(text.FieldRequired),
          lastName: yup.string().required(text.FieldRequired),
          nick: yup.string().required(text.FieldRequired),
          email: yup.string().required(text.FieldRequired),
          acceptanceYear: yup.string().required(text.FieldRequired),
          websites: yup.array().of(yup.object())
        })}
        keysOrder={[
          "firstName",
          "lastName",
          "nick",
          "email",
          "acceptanceYear",
          "websites"
        ]}
        keysComponentData={{
          firstName: {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.FirstName
            }
          },

          lastName: {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.LastName
            }
          },
          nick: {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.Nick
            }
          },
          email: {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.Email
            }
          },
          acceptanceYear: {
            component: GammaSelect,
            componentProps: {
              upperLabel: text.AcceptanceYear,
              valueToTextMap: _generateAcceptanceYears(),
              reverse: true
            }
          },
          websites: {
            array: true,
            component: EditWebsites,
            componentProps: {
              availableWebsites: availableWebsites
            }
          }
        }}
      />
    )}
  />
);

export default UserForm;