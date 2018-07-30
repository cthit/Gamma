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
import translations from "../../../../../common/declaratives/gamma-translations";

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

const EditUserInformation = ({
  initialValues,
  onSubmit,
  titleText,
  submitText
}) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Users.Screen.CommonViews.EditUserInformation"
    render={text => (
      <GammaEditData
        titleText={titleText}
        submitText={submitText}
        initialValues={initialValues}
        onSubmit={(values, actions) => {
          const wrapped = {
            post: {
              ...values
            }
          };
          onSubmit(wrapped, actions);
        }}
        validationSchema={yup.object().shape({
          cid: yup.string().required(),
          firstName: yup.string().required(),
          lastName: yup.string().required(),
          nick: yup.string().required(),
          email: yup.string().required(),
          acceptanceYear: yup.string().required()
        })}
        keysOrder={[
          "cid",
          "firstName",
          "lastName",
          "nick",
          "email",
          "acceptanceYear"
        ]}
        keysComponentData={{
          cid: {
            component: GammaTextField,
            componentProps: {
              upperLabel: text.Cid
            }
          },
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
          }
        }}
      />
    )}
  />
);

export default EditUserInformation;
