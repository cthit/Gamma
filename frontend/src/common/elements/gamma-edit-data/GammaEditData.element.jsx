import React from "react";
import * as yup from "yup";
import GammaForm from "../gamma-form";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardButtons,
  GammaCardBody,
  GammaCardSubTitle
} from "../../../common-ui/design";
import GammaFormField from "../gamma-form-field";
import GammaFormFieldArray from "../gamma-form-field-array";
import GammaButton from "../gamma-button";
import IfElseRendering from "../../declaratives/if-else-rendering";
import { FieldArray } from "formik";
const GammaEditData = ({
  initialValues,
  validationSchema,
  onSubmit,
  keysOrder,
  keysComponentData,
  titleText,
  submitText
}) => (
  <IfElseRendering
    test={initialValues != null}
    ifRender={() => (
      <GammaForm
        validationSchema={validationSchema}
        initialValues={initialValues}
        onSubmit={onSubmit}
        render={({ isSubmitting, isValid }) => (
          <GammaCard minWidth="300px" maxWidth="600px">
            <GammaCardTitle text={titleText} />
            <GammaCardBody>
              {keysOrder.map(key => {
                const keyComponentData = keysComponentData[key];
                if (keyComponentData.array == null || !keyComponentData.array) {
                  return (
                    <GammaFormField
                      key={key}
                      name={key}
                      component={keyComponentData.component}
                      componentProps={keyComponentData.componentProps}
                    />
                  );
                } else {
                  return (
                    <GammaFormFieldArray
                      key={key}
                      name={key}
                      component={keyComponentData.component}
                      componentProps={keyComponentData.componentProps}
                    />
                  );
                }
              })}
            </GammaCardBody>
            <GammaCardButtons reverseDirection>
              <GammaButton
                disabled={isSubmitting || !isValid}
                submit
                text={submitText}
                raised
                primary
              />
            </GammaCardButtons>
          </GammaCard>
        )}
      />
    )}
  />
);

export default GammaEditData;
