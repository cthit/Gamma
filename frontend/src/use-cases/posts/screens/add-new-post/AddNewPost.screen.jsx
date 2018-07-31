import React from "react";
import EditPost from "../common-views/edit-post-view";
import { Center } from "../../../../common-ui/layout";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";

import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./AddNewPost.screen.translations.json";

const AddNewPost = ({ postsAdd, toastOpen }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Posts.Screen.AddNewPost"
    render={text => (
      <Center>
        <EditPost
          onSubmit={(values, actions) => {
            postsAdd(values)
              .then(response => {
                toastOpen({ text: text.PostAdded });
                actions.resetForm();
              })
              .catch(error => {
                const code = statusCode(error);
                const message = statusMessage(error);
                switch (code) {
                  case 422:
                    switch (message) {
                      case "POST_ALREADY_EXISTS":
                        toastOpen({
                          text: text.PostAlreadyExists,
                          duration: 6000
                        });
                        actions.setErrors({ sv: "", en: "" });
                    }
                  default:
                    toastOpen({
                      text: text.SomethingWentWrong,
                      duration: 10000
                    });
                }
              });
          }}
          initialValues={{ sv: "", en: "" }}
          titleText={text.AddNewPost}
          swedishInputText={text.SwedishInput}
          englishInputText={text.EnglishInput}
          submitText={text.CreatePost}
          fieldRequiredText={text.FieldRequired}
        />
      </Center>
    )}
  />
);

export default AddNewPost;