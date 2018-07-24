import React from "react";
import * as yup from "yup";
import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";
import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaCardButtons
} from "../../../../common-ui/design";
import GammaTextField from "../../../../common/elements/gamma-text-field";
import GammaButton from "../../../../common/elements/gamma-button";
import { Center } from "../../../../common-ui/layout";

const AddNewPost = ({ postsAdd, text }) => (
  <Center>
    <GammaForm
      validationSchema={yup.object().shape({
        sv: yup.string().required(text.FieldRequired),
        en: yup.string().required(text.FieldRequired)
      })}
      initialValues={{ sv: "", en: "" }}
      onSubmit={(values, actions) => {
        const wrapped = {
          post: {
            ...values
          }
        };

        console.log(JSON.stringify(wrapped));

        postsAdd(wrapped);
      }}
      render={({ errors, touched }) => (
        <GammaCard minWidth="300px" maxWidth="600px">
          <GammaCardTitle text={text.AddNewPost} />
          <GammaCardBody>
            <GammaFormField
              name="sv"
              component={GammaTextField}
              componentProps={{ upperLabel: text.SwedishInput }}
            />

            <GammaFormField
              name="en"
              component={GammaTextField}
              componentProps={{ upperLabel: text.EnglishInput }}
            />
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <GammaButton submit text={text.CreatePost} raised primary />
          </GammaCardButtons>
        </GammaCard>
      )}
    />
  </Center>
);

export default AddNewPost;
