import React from "react";
import * as yup from "yup";
import GammaForm from "../gamma-form";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardButtons,
  GammaCardBody
} from "../../../common-ui/design";
import GammaFormField from "../gamma-form-field";
import GammaButton from "../gamma-button";

const GammaEditData = ({
  initialValues,
  validationSchema,
  onSubmit,
  keysOrder,
  keysComponentData,
  titleText,
  submitText
}) => (
  <GammaForm
    validationSchema={validationSchema}
    initialValues={initialValues}
    onSubmit={onSubmit}
    render={({ errors, touched }) => (
      <GammaCard minWidth="300px" maxWidth="600px">
        <GammaCardTitle text={titleText} />
        <GammaCardBody>
          {keysOrder.map(key => {
            const keyComponentData = keysComponentData[key];
            return (
              <GammaFormField
                key={key}
                name={key}
                component={keyComponentData.component}
                componentProps={keyComponentData.componentProps}
              />
            );
          })}
        </GammaCardBody>
        <GammaCardButtons reverseDirection>
          <GammaButton submit text={submitText} raised primary />
        </GammaCardButtons>
      </GammaCard>
    )}
  />
);

export default GammaEditData;
