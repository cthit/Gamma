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
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import EditPost from "../common-views/edit-post-view";

const EditPostDetails = ({ postsChange, post, text, match }) => (
  <IfElseRendering
    test={post != null}
    ifRender={() => (
      <Center>
        <EditPost
          onSubmit={(values, actions) => {
            postsChange(values, match.params.id);
          }}
          initialValues={{
            sv: post.sv == null ? "" : post.sv,
            en: post.en == null ? "" : post.en
          }}
          titleText={text.UpdatePost}
          swedishInputText={text.SwedishInput}
          englishInputText={text.EnglishInput}
          submitText={text.SavePost}
          fieldRequiredText={text.fieldRequiredText}
        />
      </Center>
    )}
  />
);

export default EditPostDetails;
