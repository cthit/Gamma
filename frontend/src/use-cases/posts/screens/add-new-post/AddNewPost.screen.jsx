import React from "react";
import EditPost from "../common-views/edit-post-view";
import { Center } from "../../../../common-ui/layout";

const AddNewPost = ({ postsAdd, text }) => (
  <Center>
    <EditPost
      onSubmit={(values, actions) => {
        postsAdd(values);
      }}
      initialValues={{ sv: "", en: "" }}
      titleText={text.AddNewPost}
      swedishInputText={text.SwedishInput}
      englishInputText={text.EnglishInput}
      submitText={text.CreatePost}
      fieldRequiredText={text.FieldRequired}
    />
  </Center>
);

export default AddNewPost;
