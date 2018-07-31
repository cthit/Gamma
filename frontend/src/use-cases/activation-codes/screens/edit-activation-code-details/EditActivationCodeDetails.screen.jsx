import React from "react";
import * as yup from "yup";
import GammaForm from "../../../../common/elements/gamma-form";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardButtons,
  GammaCardBody
} from "../../../../common-ui/design";
import GammaFormField from "../../../../common/elements/gamma-form-field";
import GammaButton from "../../../../common/elements/gamma-button";
import GammaTextField from "../../../../common/elements/gamma-text-field";
import GammaEditData from "../../../../common/elements/gamma-edit-data";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./EditActivationCodeDetails.screen.translations.json";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Center } from "../../../../common-ui/layout";

const EditActivationCodeDetails = ({
  activationCode,
  activationCodesChange
}) => (
  <IfElseRendering
    test={activationCode != null}
    ifRender={text => (
      <GammaTranslations
        translations={translations}
        uniquePath="ActivationCodes.Screen.EditActivationCodeDetails"
        render={text => (
          <Center>
            <GammaEditData
              initialValues={activationCode}
              onSubmit={(values, actions) => {
                console.log(values);
              }}
              validationSchema={yup.object().shape({
                code: yup.string().required(text.FieldRequired)
              })}
              titleText={text.EditActivationCode}
              submitText={text.SaveActivationCode}
              keysOrder={["code"]}
              keysComponentData={{
                code: {
                  component: GammaTextField,
                  componentProps: {
                    upperLabel: text.Code
                  }
                }
              }}
            />
          </Center>
        )}
      />
    )}
  />
);

export default EditActivationCodeDetails;